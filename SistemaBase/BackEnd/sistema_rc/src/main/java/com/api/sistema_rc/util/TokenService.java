package com.api.sistema_rc.util;

import com.api.sistema_rc.model.User;
import com.api.sistema_rc.model.UserDetailsImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String generateToken(UserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("sistema_rc")
                    .withSubject(user.getLogin())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        }catch (JWTCreationException exception){
            throw new RuntimeException("Error while generating token",exception);
        }
    }

    public Integer validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String user_id = JWT.require(algorithm)
                    .withIssuer("sistema_rc")
                    .build()
                    .verify(token.replace("Bearer ", ""))
                    .getSubject();
            return Integer.parseInt(user_id);
        }catch (JWTVerificationException exception){
            return -1;
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("-03:00"));
    }
}