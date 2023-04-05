package com.cst8277.twiiter.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cst8277.twiiter.Service.UserManagementService;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.sql.SQLException;
import java.util.Date;

@Configuration
@EnableWebSecurity
public class Security {
    @Bean
    @Autowired
    SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf().disable();
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();

        httpSecurity.oauth2Login()
                .defaultSuccessUrl("/user", true);
        return httpSecurity.build();

    }
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        // Create a new client registration
        return new InMemoryClientRegistrationRepository(

                CommonOAuth2Provider.GITHUB.getBuilder("github")

                        .clientId("0691a3430c237d47b3b9")
                        .clientSecret("6ea82900662bd9050066b80fc5011a6a186e2d1e")
                        .build());
    }

    public String jwtEncode(String name) throws SQLException {
        UserManagementService usmg = new UserManagementService();
        String uuid = String.valueOf(usmg.generateToken());
        String s = JWT.create().withClaim(name,uuid).withExpiresAt(new Date(System.currentTimeMillis()+900000)).sign(Algorithm.HMAC512("s"));
        UserManagementService usms = new UserManagementService();
        usms.verifyToken(name,s);
        return s;
    }
    public boolean isJWTExpired(DecodedJWT decodedJWT){
        Date expiresAt = decodedJWT.getExpiresAt();
        return expiresAt.before(new Date());
    }


}
