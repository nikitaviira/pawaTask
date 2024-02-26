package com.pawatask.gateway.config;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawatask.gateway.exception.UnauthorizedException;
import com.pawatask.gateway.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GatewayFilter {
    private final JWTUtil jwtUtil;

    @Value("${authentication.enabled}")
    private boolean authEnabled;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!authEnabled) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (isNull(authorizationHeader)) {
            throw new UnauthorizedException("Missing credentials");
        }

        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtUtil.decodeJwt(requireNonNull(authorizationHeader).split(" ")[1]);
        } catch (Exception e) {
            throw new UnauthorizedException("Incorrect authentication token");
        }

        if (jwtUtil.isTokenExpired(decodedJWT)) {
            throw new UnauthorizedException("Authentication token has expired");
        }

        addUserContext(exchange, decodedJWT);
        return chain.filter(exchange);
    }

    private void addUserContext(ServerWebExchange exchange, DecodedJWT decodedJWT) {
        Claim userId = decodedJWT.getClaim("userId");
        exchange.getRequest()
                .mutate()
                .header("userId", userId.toString())
                .build();
    }
}