package com.cst8277.twiiter.controllers;

import com.cst8277.twiiter.dto.Greeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.atomic.AtomicLong;
@RestController

public class Controller {
private static final String template = "hello world, %s!";
private final AtomicLong counter = new AtomicLong();

@GetMapping(path="/")
    public Greeting getMainPage(@RequestParam(value = "name",defaultValue = "World") String name) {
    return new Greeting(counter.incrementAndGet(), String.format(template, name));

}
}
