package com.cst8277.twiiter.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cst8277.twiiter.Service.UserManagementService;
import com.cst8277.twiiter.dto.Greeting;
import com.cst8277.twiiter.security.Security;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.context.request.RequestContextHolder;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@SpringBootApplication
@Configuration
public class UserController {
    private static final String template = "hello world, %s!";
    private final AtomicLong counter = new AtomicLong();
    UserManagementService usmg = new UserManagementService();
    Security j = new Security();

    Security s = new Security();
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
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) throws SQLException {
        String g = j.jwtEncode(principal.getAttribute("login"));

        System.out.println(j.isJWTExpired(JWT.decode(g)));

        HashMap<String, Object> map = new HashMap<>();
        map.put(principal.getAttribute("login"),usmg.generateSession(principal.getAttribute("login"),g));

        return Collections.singletonMap("name", principal.getAttribute("login"));
    }

}
