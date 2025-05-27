// src/main/java/com/example/server/JwtTokenProvider.java

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    AppConfig
    private final String secret = ???:?;
    // e.g. never expire / 10 years:
    private final long validityMs = 10L * 365 * 24 * 60 * 60 * 1000;

    public String createToken(long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public long getExpiresInMs() { return validityMs; }
}
