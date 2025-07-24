package gift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gift.exception.InternalServerException;
import gift.exception.UnAuthorizationException;
import gift.user.JwtTokenProvider;
import gift.user.entity.Role;
import gift.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static String createExpiredToken() {
    String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

    Date expiredTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

    return Jwts.builder()
        .setSubject("1")
        .claim("email", "admin.admin.com")
        .claim("role", "ADMIN")
        .expiration(expiredTime)
        .signWith(key)
        .compact();
  }

  private static String createWrongSignatureToken() {
    String wrongSecretKey = "qwe다른아무키rtyuiopasdfghjklzxcvbnm";
    SecretKey wrongKey = Keys.hmacShaKeyFor(wrongSecretKey.getBytes());

    Date futureTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

    return Jwts.builder()
        .setSubject("1")
        .claim("email", "admin.admin@com")
        .claim("role", "ADMIN")
        .expiration(futureTime)
        .signWith(wrongKey)
        .compact();
  }

  @Test
  void 토큰_생성_성공() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    User testUser = new User(1L, "admin@admin.com", "password", Role.ADMIN);

    //when
    String token = jwtTokenProvider.generateToken(testUser);

    //then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
  }

  @Test
  void 만료된_토큰() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createExpiredToken();

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.validateToken(token));
  }

  @Test
  void 잘못된_서명_토큰_검증_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createWrongSignatureToken();

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.validateToken(token));
  }

  @Test
  void 잘못된_형식_토큰_검증_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = "wrongFormatToken";

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.validateToken(token));
  }

  @Test
  void null_토큰_검증_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = null;

    assertThrows(InternalServerException.class,
        () -> jwtTokenProvider.validateToken(token));
  }

  @Test
  void 유효한_토큰에서_role_추출_성공() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    User testUser = new User(1L, "admin@admin.com", "password", Role.ADMIN);
    String token = jwtTokenProvider.generateToken(testUser);

    String role = jwtTokenProvider.getRole(token);

    assertThat(role).isEqualTo("ADMIN");
  }

  @Test
  void 유효한_토큰에서_email_추출_성공() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    User testUser = new User(1L, "admin@admin.com", "password", Role.ADMIN);
    String token = jwtTokenProvider.generateToken(testUser);

    String email = jwtTokenProvider.getEmail(token);

    assertThat(email).isEqualTo("admin@admin.com");
  }

  @Test
  void 잘못된_서명_토큰에서_role_추출_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createWrongSignatureToken();

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.getRole(token));
  }

  @Test
  void 만료된_토큰에서_role_추출_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createExpiredToken();


    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.getRole(token));
  }

  @Test
  void null_토큰에서_role_추출_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = null;

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.getRole(token));
  }

  @Test
  void 빈문자열_토큰에서_role_추출_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = "";

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.getRole(token));
  }

  @Test
  void 잘못된_서명_토큰에서_email_추출_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createWrongSignatureToken();

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.getEmail(token));
  }

  @Test
  void null_토큰에서_email_추출_실패() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = null;

    assertThrows(UnAuthorizationException.class,
        () -> jwtTokenProvider.getEmail(token));
  }
}