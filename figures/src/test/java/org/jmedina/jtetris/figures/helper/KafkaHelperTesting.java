package org.jmedina.jtetris.figures.helper;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.kafka.listener.MessageListenerTesting;
import org.jmedina.jtetris.figures.util.AssertUtilTesting;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.ContainerGroup;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * @author Jorge Medina
 *
 */
public class KafkaHelperTesting {

	protected final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.groupId.messageTest}")
	public String messageTestGroupId;

	@Value("${figures.topic.nextFigure}")
	public String topicNextFigure;

	@Autowired
	protected MessageListenerTesting messageListenerTesting;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	@SuppressWarnings("rawtypes")
	@Autowired
	private KafkaListenerContainerFactory kafkaListenerContainerFactory;

	@Autowired
	private AssertUtilTesting assertUtil;

	private CountDownLatch latch = new CountDownLatch(1);

	@BeforeAll
	protected void startListener() throws InterruptedException {
		this.logger.debug("==> KafkaHelperTesting.startListener() =" + this.messageListenerTesting);
		if (!this.listenerIsRunning()) {
			this.createAndRegisterListener();
		}
	}

	@AfterAll
	protected void clearListener() throws InterruptedException {
		this.logger.debug("==> KafkaHelperTesting.clearListener() =" + this.messageListenerTesting);
		this.unRegisterListener();
		this.logger.debug("==> KafkaHelperTesting.clearListener() ==> DONE");
	}

	@BeforeEach
	protected void resetState() {
		this.assertUtil.awaitOneSecond();
		this.resetMessageListener();
	}

	protected void resetMessageListener() {
		this.messageListenerTesting.resetMessageListener();
	}

	protected void assertMessageListenerLatch() throws InterruptedException {
		this.assertUtil.assertLatch(this.messageListenerTesting.getLatch());
	}

	protected void assertMessageCaja() {
		this.assertUtil.assertMessageCaja(this.messageListenerTesting.getMessage());
	}

	protected void assertMessageEle() {
		this.assertUtil.assertMessageEle(this.messageListenerTesting.getMessage());
	}

	protected void assertMessageFigure() {
		this.assertUtil.assertMessageFigura(this.messageListenerTesting.getMessage());
	}

	private void createAndRegisterListener() {
		kafkaListenerEndpointRegistry.registerListenerContainer(createKafkaListenerEndpoint(),
				kafkaListenerContainerFactory, true);
	}

	@SuppressWarnings("unchecked")
	private boolean listenerIsRunning() throws InterruptedException {
		AtomicReference<MessageListenerContainer> listenerRef = new AtomicReference<MessageListenerContainer>();
		List<MessageListenerContainer> groupList = (List<MessageListenerContainer>) context.getBean("endpointGroup");
		if (Objects.nonNull(groupList)) {
			groupList.stream().forEach(l -> {
				listenerRef.set(l);
			});
			MessageListenerContainer listener = listenerRef.get();
			if (Objects.nonNull(listener)) {
				if (listener.isRunning()) {
					this.logger.debug("==> listener = " + listener);
					return true;
				}
				this.unRegisterListener();
			}
		}
		return false;
	}

	private KafkaListenerEndpoint createKafkaListenerEndpoint() {
		MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint = createDefaultMethodKafkaListenerEndpoint();
		kafkaListenerEndpoint.setBean(this.messageListenerTesting = new MessageListenerTesting());
		kafkaListenerEndpoint.setGroup("endpointGroup");
		try {
			kafkaListenerEndpoint.setMethod(
					MessageListenerTesting.class.getMethod("listenGroupFigureMessageTest", ConsumerRecord.class));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Attempt to call a non-existent method " + e);
		}
		return kafkaListenerEndpoint;
	}

	private MethodKafkaListenerEndpoint<String, String> createDefaultMethodKafkaListenerEndpoint() {
		MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint = new MethodKafkaListenerEndpoint<>();
		kafkaListenerEndpoint.setId("kafkaListener");
		kafkaListenerEndpoint.setGroupId(messageTestGroupId);
		kafkaListenerEndpoint.setAutoStartup(true);
		kafkaListenerEndpoint.setTopics(topicNextFigure);
		kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());
		return kafkaListenerEndpoint;
	}

	@SuppressWarnings("unchecked")
	private void unRegisterListener() throws InterruptedException {
		AtomicReference<MessageListenerContainer> listener = new AtomicReference<MessageListenerContainer>();
		List<MessageListenerContainer> groupList = (List<MessageListenerContainer>) context.getBean("endpointGroup");
		groupList.stream().forEach(l -> {
			listener.set(l);
			l.stop(() -> {
				l.destroy();
				this.latch.countDown();
			});
		});
		kafkaListenerEndpointRegistry.unregisterListenerContainer("kafkaListener");
		groupList.remove(listener.get());
		ContainerGroup containerGroup = (ContainerGroup) context.getBean("endpointGroup.group");
		containerGroup.removeContainer(listener.get());
		containerGroup.stop();
		assertTrue(this.latch.await(10, TimeUnit.SECONDS));
	}

}