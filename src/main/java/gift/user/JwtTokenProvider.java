package gift.user;

import gift.exception.ErrorCode;
import gift.exception.InternalServerException;
import gift.exception.UnAuthorizationException;
import gift.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
  private final SecretKey secretKeySpec;
  private final JwtParser jwtParser;

  public JwtTokenProvider() {

    this.secretKeySpec = Keys.hmacShaKeyFor(secretKey.getBytes());

    this.jwtParser = Jwts.parser()
        .verifyWith(secretKeySpec)
        .build();
  }

  public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().toString())
        .expiration(createExpirationDate())
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
        .compact();
  }

  private Date createExpirationDate() {
    long EXPIRATION_MINUTES = 30;
    return new Date(System.currentTimeMillis() + EXPIRATION_MINUTES * 60 * 1000);
  }

  public void validateToken(String token) {
    try {
      jwtParser.parse(token);
    } catch (ExpiredJwtException | SignatureException | MalformedJwtException |
             SecurityException e) {
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    } catch (Exception e) {
      throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  public String getRole(String token) {
    try {
      Claims claims = jwtParser.parseSignedClaims(token).getPayload();
      return claims.get("role", String.class);
    } catch (JwtException | IllegalArgumentException e) {
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    } catch (Exception e) {
      throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  public String getEmail(String token) {
    try {
      Claims claims = jwtParser.parseSignedClaims(token).getPayload();
      return claims.get("email", String.class);
    } catch (JwtException | IllegalArgumentException e) {
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    } catch (Exception e) {
      throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

}
