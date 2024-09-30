package com.master.flow.config;

import com.master.flow.model.vo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class TokenProvider {
    private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String create(User user){
        return Jwts.builder()
                .signWith(secretKey)
                .setClaims(Map.of(
                        "userCode", user.getUserCode(),
                        "userEmail", user.getUserEmail(),
                        "userPlatform", user.getUserPlatform()
                ))
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .compact();
    }

    public User validate(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return User.builder()
                .userCode((Integer) claims.get("userCode"))
                .userEmail((String) claims.get("userEmail"))
                .userPlatform((String) claims.get("userPlatform"))
                .build();
    }
}
