package com.cst8277.twiiter.controllers;

import com.cst8277.twiiter.Service.UserManagementService;
import com.cst8277.twiiter.dto.Greeting;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController

public class UserController {
    private static final String template = "hello world, %s!";
    private final AtomicLong counter = new AtomicLong();
    UserManagementService usmg = new UserManagementService();

    @GetMapping(path = "/")
    public ResponseEntity<Greeting> getMainPage(@RequestParam("uuid") String uuid, @RequestParam(value = "name", defaultValue = "World") String name) throws SQLException {
        if (!usmg.verifyToken(name, uuid)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Greeting(counter.incrementAndGet(), String.format(template, name)));


    }
    @GetMapping(path = "/users")
    public ResponseEntity<Map<String, Object>> User(@RequestParam("name") String name, @RequestParam("uuid") String uuid) throws SQLException {

        HashMap<String, Object> map = usmg.getUsers(name,uuid);

        return ResponseEntity.status(uuid != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(map);

    }
    @GetMapping(path = "/login")
    public ResponseEntity<Map<String, Object>> Login(@RequestParam("name") String name, @RequestParam("password") String password) throws SQLException {
        String uuid = usmg.login(name, password);
        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);

        return ResponseEntity.status(uuid != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(map);

    }
}
