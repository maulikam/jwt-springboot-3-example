package com.example.oauth.authorization.example.controller;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class ExampleController {

	@RequestMapping("/example")
	public String returnAnything() {
		log.warn("Example Message: in Example Controller");
		return "Example Message: in Example Controller";
	}

}
