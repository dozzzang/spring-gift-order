package gift.security;

import gift.user.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  public AdminInterceptor(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }


  public boolean isAdmin(HttpServletRequest req, HttpServletResponse res)
      throws Exception {

    String token = req.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (isValidAdminToken(token)) {
      return true;
    }

    res.setStatus(HttpStatus.FORBIDDEN.value());
    return false;
  }

  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse res,
      Object handler) throws Exception {
    return isAdmin(req, res);
  }

  private boolean isValidAdminToken(String token) throws Exception {
    jwtTokenProvider.validateToken(token);
    String role = jwtTokenProvider.getRole(token);
    return role.equals("ADMIN");
  }
}
