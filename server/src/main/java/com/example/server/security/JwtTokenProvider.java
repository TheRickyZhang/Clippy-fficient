package com.example.server.security;

import com.example.shared.Env;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secret     = Env.get("jwt.secret");
    private final long   validityMs = Long.parseLong(Env.get("jwt.validityMs"));

    public String createToken(long userId) {
        var now = new Date();
        var exp = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public long getExpiresInMs() { return validityMs; }
}
