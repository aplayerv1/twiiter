package com.cst8277.twiiter.controllers;

import com.cst8277.twiiter.Service.UserManagementService;
import com.cst8277.twiiter.dto.Greeting;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
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
    @GetMapping(path = "/subscriber")
    public ResponseEntity<Map<String, Object>> Subscriber(@RequestParam("name") String name, @RequestParam("uuid") String uuid) throws SQLException {
        HashMap<String, Object> map = usmg.getSubs(name,uuid);
        return ResponseEntity.status(uuid != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(map);

    }
    @GetMapping(path = "/addUser")
    public ResponseEntity<Map<String, Object>> addUser(@RequestParam("name") String name, @RequestParam("uuid") String uuid,@RequestParam("username")String username,@RequestParam("password") String password) throws SQLException{
        HashMap<String,Object> map = usmg.addUser(name,uuid,username,password);
        return ResponseEntity.status(uuid != null ?HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(map);
    }
    @GetMapping(path = "/login")
    public ResponseEntity<Map<String, Object>> Login(@RequestParam("name") String name, @RequestParam("password") String password) throws SQLException {
        String uuid = usmg.login(name, password);
        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        return ResponseEntity.status(uuid != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(map);
    }

    @GetMapping(path = "/getMsg")
    public ResponseEntity<Map<String,Object>> getMsg(@RequestParam("name") String name, @RequestParam("uuid")String uuid,@RequestParam("username")String username) throws SQLException{
        HashMap<String, Object> map = usmg.getMsg(name,uuid,username);
        return ResponseEntity.status(uuid != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(map);

    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
}
