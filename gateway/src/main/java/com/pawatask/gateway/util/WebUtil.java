package com.pawatask.gateway.util;

import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetSocketAddress;

public class WebUtil {
  public static String getIpAddress(ServerWebExchange exchange) {
    XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(1);
    InetSocketAddress inetSocketAddress = resolver.resolve(exchange);
    return inetSocketAddress.getAddress().getHostAddress();
  }
}
