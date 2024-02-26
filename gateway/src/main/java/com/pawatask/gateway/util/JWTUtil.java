package com.pawatask.gateway.util;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JWTUtil {
  private final Algorithm algorithm;

  public JWTUtil(@Value("${token.secret}") String secret) {
    this.algorithm = HMAC512(secret);
  }

  public DecodedJWT decodeJwt(String token) {
    return require(algorithm).build().verify(token);
  }

  public boolean isTokenExpired(DecodedJWT decodedJWT) {
    Date expirationDate = decodedJWT.getExpiresAt();
    return expirationDate != null && expirationDate.before(new Date());
  }
}