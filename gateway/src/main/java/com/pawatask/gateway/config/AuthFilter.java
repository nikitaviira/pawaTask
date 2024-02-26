package com.pawatask.gateway.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class AuthFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return null;
    }
//
//    @Autowired
//    RouteValidator routeValidator;
//
//    @Autowired
//    private JWTUtil jwtUtil;
//
//    @Autowired
//    private AuthUtil authUtil;
//
//    @Value("${authentication.enabled}")
//    private boolean authEnabled;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        if (!authEnabled) {
//            return chain.filter(exchange);
//        }
//
//        String token;
//        ServerHttpRequest request = exchange.getRequest();
//
//        if (routeValidator.isSecured.test(request)) {
//            if (this.isCredsMissing(request)) {
//                System.out.println("in error");
//                return this.onError(exchange,"Credentials missing");
//            }
//            if (request.getHeaders().containsKey("userName") && request.getHeaders().containsKey("role")) {
//                token = authUtil.getToken(request.getHeaders().get("userName").toString(), request.getHeaders().get("role").toString());
//            }
//            else {
//                token = request.getHeaders().get("Authorization").toString().split(" ")[1];
//            }
//
//            if (jwtUtil.isInvalid(token)) {
//                return this.onError(exchange,"Auth header invalid");
//            }
//
//            this.populateRequestWithHeaders(exchange,token);
//        }
//        return chain.filter(exchange);
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange, String err) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        return response.setComplete();
//    }
//
//    private String getAuthHeader(ServerHttpRequest request) {
//        return request.getHeaders().getOrEmpty("Authorization").get(0);
//    }
//
//    private boolean isCredsMissing(ServerHttpRequest request) {
//        return !(request.getHeaders().containsKey("userName") && request.getHeaders().containsKey("role")) && !request.getHeaders().containsKey("Authorization");
//    }
//
//    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
//        Claims claims = jwtUtil.getALlClaims(token);
//        exchange.getRequest()
//                .mutate()
//                .header("userId", String.valueOf(claims.get("id")))
//                .build();
//    }
}