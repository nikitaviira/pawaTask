package com.pawatask.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pawatask.auth.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.time.temporal.ChronoUnit.HOURS;

@Component
public class JwtGeneration {
  private static final long EXPIRATION_IN_HOURS = 24L;

  private final Algorithm algorithm;
  private final String issuer;

  public JwtGeneration(@Value("${token.secret}") String secret,
                       @Value("${token.issuer}") String issuer) {
    this.algorithm = HMAC512(secret);
    this.issuer = issuer;
  }

  public String generate(User user) {
    Instant now = Instant.now();
    return JWT.create()
      .withClaim("userId", user.getId())
      .withClaim("email", user.getEmail())
      .withIssuer(issuer)
      .withIssuedAt(Date.from(now))
      .withExpiresAt(Date.from(now.plus(EXPIRATION_IN_HOURS, HOURS)))
      .sign(algorithm);
  }
}