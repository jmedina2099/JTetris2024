package org.jmedina.jtetris.api.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Jorge Medina
 *
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final boolean showHeaders = false;

	@GetMapping(value = "/credentials", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object user(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> UserController.user() = " + (Objects.nonNull(user) ? user.getName() : user));
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		return new Object() {
			@SuppressWarnings("unused")
			public String name = (Objects.nonNull(user) ? user.getName() : "");
		};
	}

	@GetMapping(value = "/resource", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> home(Principal user) {
		this.logger.debug("===> UserController.home()");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("id", UUID.randomUUID().toString());
		model.put("username", (Objects.nonNull(user) ? user.getName() : ""));
		return model;
	}

	private void printHeaders(ServerWebExchange exchange) {
		this.logger.info("=========> HEADERS!!!");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			List<String> values = headers.get(key);
			if (values != null) {
				values.stream().forEach(v -> {
					this.logger.info("=========> header= {}: {}", key, v);
				});
			}
		});
		this.logger.info("=========> END HEADERS!!!");
	}

}
