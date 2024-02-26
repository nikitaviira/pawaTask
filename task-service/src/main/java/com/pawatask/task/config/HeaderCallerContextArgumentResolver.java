package com.pawatask.task.config;

import com.pawatask.task.dto.CallerContext;
import com.pawatask.task.exception.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static java.util.Optional.ofNullable;

@Component
public class HeaderCallerContextArgumentResolver implements HandlerMethodArgumentResolver {
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(CallerContext.class);
  }

  @Override
  @Nullable
  public Object resolveArgument(MethodParameter parameter,
                                @Nullable ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                @Nullable WebDataBinderFactory binderFactory) {
    return ofNullable(webRequest.getHeader("userId"))
        .map(userId -> new CallerContext(Long.valueOf(userId)))
        .orElseThrow(() -> new UnauthorizedException("User context is missing"));
  }
}