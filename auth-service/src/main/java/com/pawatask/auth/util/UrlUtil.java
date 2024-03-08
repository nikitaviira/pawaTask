package com.pawatask.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlUtil {
  @Value("${app.clientUrl}")
  private String appClientUrl;

  public String absoluteUrl(String url) {
    return absoluteURL(appClientUrl, url);
  }

  private String absoluteURL(String baseUrl, String relativeUrl) {
    if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    if (relativeUrl.startsWith("/")) relativeUrl = relativeUrl.substring(1);
    return baseUrl + "/" + relativeUrl;
  }
}
