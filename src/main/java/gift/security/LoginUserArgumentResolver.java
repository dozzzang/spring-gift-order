package gift.security;

import gift.exception.ErrorCode;
import gift.exception.UnAuthorizationException;
import gift.user.service.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

  private final UserService userService;

  public LoginUserArgumentResolver(UserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoginUser.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) throws Exception {

    var authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorization == null || authorization.isBlank() || !authorization.startsWith("Bearer ")) {
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    }

    String token = authorization.substring(7);

    if (token.isBlank()) {
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    }

    return userService.findUserByToken(token);
  }


}
