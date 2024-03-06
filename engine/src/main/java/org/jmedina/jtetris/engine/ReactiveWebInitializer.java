package org.jmedina.jtetris.engine;

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
		return new Class[] { EngineApplication.class };
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		return new SpringApplication(getConfigClasses()).run();
	}
}
