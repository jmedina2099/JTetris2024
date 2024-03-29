package org.jmedina.jtetris.gateway;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;

/**
 * @author Jorge Medina
 *
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = JerseyServerMetricsAutoConfiguration.class)
public class GatewayServerApplication {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		this.logger.debug("===> GatewayServerApplication.customRouteLocator");
		return builder.routes()
				.route("serviceApi", r -> r.path("/service-api/**").filters(corsFilters()).uri("lb://API"))
				.route("serviceEngine",
						r -> r.path("/service-engine/**").filters(corsFilters()).uri("lb://ENGINE"))
				.route("serviceFigures",
						r -> r.path("/service-figures/**").filters(corsFilters()).uri("lb://FIGURES"))
				.build();
	}
	
	@Bean
	Function<GatewayFilterSpec, UriSpec> corsFilters() {
        return f -> f
        		.stripPrefix(1)
        		.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
        		.dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE");
    }

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

}