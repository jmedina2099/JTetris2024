package org.jmedina.jtetris.api.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jorge Medina
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = "/credentials", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object user(Principal user) {
    	this.logger.debug("===> UserController.user() = " + (Objects.nonNull(user)? user.getName(): user) );
        return new Object() {
        	@SuppressWarnings("unused")
			public String name = (Objects.nonNull(user)? user.getName(): "");
        };
    }

    @GetMapping(value = "/resource", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> home() {
    	this.logger.debug("===> UserController.home()" );
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }
}
