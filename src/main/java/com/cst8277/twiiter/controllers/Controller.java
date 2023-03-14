package com.cst8277.twiiter.controllers;

import com.cst8277.twiiter.Service.UserManagementService;
import com.cst8277.twiiter.dto.Greeting;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController

public class Controller {
    private static final String template = "hello world, %s!";
    private final AtomicLong counter = new AtomicLong();
    UserManagementService usmg = new UserManagementService();

    @GetMapping(path = "/")
    public ResponseEntity<Greeting> getMainPage(@RequestParam("uuid") String uuid, @RequestParam(value = "name", defaultValue = "World") String name) throws SQLException {
        if (!usmg.checkauth(name,uuid)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Greeting(counter.incrementAndGet(), String.format(template, name))) ;


    }
    @GetMapping(path = "/login")
    public ResponseEntity<UUID> Login(@RequestParam("name") String name, @RequestParam("password") String password) throws SQLException {
      UUID uuid = usmg.validate(name,password);

      return ResponseEntity.status(uuid!=null ? HttpStatus.OK:HttpStatus.UNAUTHORIZED).body(uuid);

    }
}
