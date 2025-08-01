package gift.auth.util;

import gift.exception.ErrorCode;
import gift.exception.UnAuthorizationException;
import gift.user.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

  private static final Logger log = LoggerFactory.getLogger(AuthUtil.class);
  private final JwtTokenProvider jwtTokenProvider;

  public AuthUtil(final JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public Long getUserIdFromRequest(final HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.error("Authorization 헤더가 비어있습니다.");
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    }

    final String token = authHeader.substring(7);
    jwtTokenProvider.validateToken(token);
    return jwtTokenProvider.getUserId(token);
  }
}
