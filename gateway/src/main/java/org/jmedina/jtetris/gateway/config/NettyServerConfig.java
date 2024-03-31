package org.jmedina.jtetris.gateway.config;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

/**
 * @author Jorge Medina
 *
 */
@Configuration
public class NettyServerConfig {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	NettyServerCustomizer nettyServerCustomizer() {
		return httpServer -> httpServer.idleTimeout(Duration.ofHours(1)).readTimeout(Duration.ofHours(1))
				.requestTimeout(Duration.ofHours(1)).maxKeepAliveRequests(10);
	}

	@Bean
	WebServerFactoryCustomizer<NettyReactiveWebServerFactory> idleTimeoutCustomizer(
			@Value("${server.netty.connection-timeout}") Duration connectionTimeout,
			@Value("${server.netty.idle-timeout}") Duration idleTimeout,
			@Value("${server.netty.read-timeout}") Duration readTimeout,
			@Value("${server.netty.write-timeout}") Duration writeTimeout) {

		this.logger.debug("===> GATEWAY - connectionTimeout = " + connectionTimeout.toMinutes());
		this.logger.debug("===> GATEWAY - idleTimeout = " + idleTimeout.toMinutes());
		this.logger.debug("===> GATEWAY - readTimeout = " + readTimeout.toMinutes());
		this.logger.debug("===> GATEWAY - writeTimeout = " + writeTimeout.toMinutes());

		return factory -> factory.addServerCustomizers(
				tcp -> tcp.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectionTimeout.toMillis())
						.doOnChannelInit((connectionObserver, channel, remoteAddress) -> channel.pipeline()
								.addFirst(new IdleStateHandler((int) readTimeout.toSeconds(),
										(int) writeTimeout.toSeconds(), (int) idleTimeout.toSeconds()) {
									private final AtomicBoolean closed = new AtomicBoolean();

									@Override
									protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
										if (closed.compareAndSet(false, true)) {
											ctx.close();
										}
									}
								}))
						.doOnConnection(conn -> {
							conn.channel().pipeline().addLast(new WriteTimeoutHandler((int) writeTimeout.toSeconds()))
									.addLast(new ReadTimeoutHandler((int) readTimeout.toSeconds()));
						}));
	}
}
