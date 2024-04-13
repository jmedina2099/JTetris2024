package org.jmedina.jtetris.engine.config;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;

/**
 * @author Jorge Medina
 *
 */
@Component
public class CustomWebServerFactory extends NettyReactiveWebServerFactory {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${server.netty.connection-timeout}")
	private Duration connectionTimeout;

	@Value("${server.netty.idle-timeout}")
	private Duration idleTimeout;

	@Value("${server.netty.read-timeout}")
	private Duration readTimeout;

	@Value("${server.netty.write-timeout}")
	private Duration writeTimeout;

	public CustomWebServerFactory() {
		super();
	}

	@PostConstruct
	private void init() {

		this.logger.debug("===> ENGINE - connectionTimeout = " + connectionTimeout.toMinutes());
		this.logger.debug("===> ENGINE - idleTimeout = " + idleTimeout.toMinutes());
		this.logger.debug("===> ENGINE - readTimeout = " + readTimeout.toMinutes());
		this.logger.debug("===> ENGINE - writeTimeout = " + writeTimeout.toMinutes());

		addServerCustomizers(httpServer -> httpServer.option(ChannelOption.AUTO_CLOSE, false)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectionTimeout.toMillis())
				.maxKeepAliveRequests(-1).idleTimeout(idleTimeout).readTimeout(readTimeout).requestTimeout(readTimeout)
				.doOnChannelInit((connectionObserver, channel, remoteAddress) -> {
					channel.pipeline().addFirst(new IdleStateHandler((int) readTimeout.toSeconds(),
							(int) writeTimeout.toSeconds(), (int) idleTimeout.toSeconds()) {
						private final AtomicBoolean closed = new AtomicBoolean();

						@Override
						protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
							if (closed.compareAndSet(false, true)) {
								ctx.close();
							}
						}
					});
				}).doOnConnection(conn -> {
					this.logger.debug("===============================> doOnConnected in ENGINE - conn.isPersistent ="
							+ conn.isPersistent());
					conn.channel().pipeline().addLast(new WriteTimeoutHandler((int) writeTimeout.toSeconds()))
							.addLast(new ReadTimeoutHandler((int) readTimeout.toSeconds()));
				}));
	}
}
