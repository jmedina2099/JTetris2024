package org.jmedina.jtetris.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.server.adapter.AbstractReactiveWebInitializer;

/**
 * @author Jorge Medina
 *
 */
public class ReactiveWebInitializer extends AbstractReactiveWebInitializer {

	@Override
	protected Class<?>[] getConfigClasses() {
		return new Class[] { GatewayServerApplication.class };
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		SpringApplication springApplication = new SpringApplication(getConfigClasses());
		return springApplication.run();
	}
}
