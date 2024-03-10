package org.jmedina.jtetris.figures.helper;

import java.util.List;

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

	@BeforeAll
	void startListener() {
		this.logger.debug("==> FigureServiceDefaultTest.startListener()");
		this.createAndRegisterListener();
	}

	@AfterAll
	void clearListener() {
		this.logger.debug("==> FigureServiceDefaultTest.clearListener()");
		this.unRegisterListener();
	}

	@BeforeEach
	void resetState() {
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

	protected void createAndRegisterListener() {
		kafkaListenerEndpointRegistry.registerListenerContainer(createKafkaListenerEndpoint(),
				kafkaListenerContainerFactory, true);
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
	protected void unRegisterListener() {
		kafkaListenerEndpointRegistry.unregisterListenerContainer("kafkaListener");
		MessageListenerContainer[] listener = new MessageListenerContainer[1];
		List<MessageListenerContainer> groupList = (List<MessageListenerContainer>) context.getBean("endpointGroup");
		groupList.stream().forEach(l -> {
			listener[0] = l;
			l.stop(() -> {
				l.destroy();
			});
		});
		groupList.remove(listener[0]);
		ContainerGroup containerGroup = (ContainerGroup) context.getBean("endpointGroup.group");
		containerGroup.removeContainer(listener[0]);
		containerGroup.stop();
	}
}