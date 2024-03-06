package org.jmedina.jtetris.figures;

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
		return new Class[] { FiguresApplication.class };
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		return new SpringApplication(getConfigClasses()).run();
	}
}
