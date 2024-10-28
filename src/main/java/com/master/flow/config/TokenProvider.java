package com.master.flow.config;

import com.master.flow.model.vo.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class TokenProvider {
    private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String create(User user){
        return Jwts.builder()
                .signWith(secretKey)
                .setClaims(Map.of(
                        "userCode", user.getUserCode(),
                        "userEmail", user.getUserEmail(),
                        "userPlatform", user.getUserPlatform(),
                        "userManagerCode", user.getUserManagerCode()
                ))
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .compact();
    }

    public User validate(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return User.builder()
                    .userCode((Integer) claims.get("userCode"))
                    .userEmail((String) claims.get("userEmail"))
                    .userPlatform((String) claims.get("userPlatform"))
                    .userManagerCode((String) claims.get("userManagerCode"))
                    .build();
        } catch (SignatureException e) {
//            throw new JwtException(ErrorMessage.WRONG_TYPE_TOKEN.getMsg());
            log.info("----------------------SignatureException--------------------");
            throw new JwtException("SignatureException");
        } catch (MalformedJwtException e) {
//            throw new JwtException(ErrorMessage.UNSUPPORTED_TOKEN.getMsg());
            log.info("----------------------MalformedJwtException--------------------");
            throw new JwtException("MalformedJwtException");
        } catch (ExpiredJwtException e) {
//            throw new JwtException(ErrorMessage.EXPIRED_TOKEN.getMsg());
            log.info("----------------------ExpiredJwtException--------------------");
            throw new JwtException("ExpiredJwtException");
        } catch (IllegalArgumentException e) {
//            throw new JwtException(ErrorMessage.UNKNOWN_ERROR.getMsg());
            log.info("----------------------IllegalArgumentException--------------------");
            throw new JwtException("IllegalArgumentException");
        }
    }
}
