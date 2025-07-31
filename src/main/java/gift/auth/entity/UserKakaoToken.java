package gift.auth.entity;

import gift.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_kakao_token")
public class UserKakaoToken {

  @Id
  private Long userId;
  @Column(nullable = false)
  private String accessToken;
  @Column(nullable = false)
  private String refreshToken;
  @Column(nullable = false)
  private Instant accessTokenExpiresAt;
  @Column(nullable = false)
  private Instant refreshTokenExpiresAt;
  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  protected UserKakaoToken() {
  }

  public static UserKakaoToken create(User user, String accessToken, String refreshToken,
      int accessTokenExpiresIn, int refreshTokenExpiresIn) {
    UserKakaoToken token = new UserKakaoToken();
    token.userId = user.getId();
    token.user = user;
    token.accessToken = accessToken;
    token.refreshToken = refreshToken;
    token.accessTokenExpiresAt = Instant.now().plusSeconds(accessTokenExpiresIn);
    token.refreshTokenExpiresAt = Instant.now().plusSeconds(refreshTokenExpiresIn);
    return token;
  }

  public void updateTokens(String newAccessToken, String newRefreshToken,
      int accessTokenExpiresIn, int refreshTokenExpiresIn) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
    this.accessTokenExpiresAt = Instant.now().plusSeconds(accessTokenExpiresIn);
    this.refreshTokenExpiresAt = Instant.now().plusSeconds(refreshTokenExpiresIn);
  }

  public boolean isAccessTokenExpired() {
    return Instant.now().isAfter(accessTokenExpiresAt);
  }

  public boolean isRefreshTokenExpired() {
    return Instant.now().isAfter(refreshTokenExpiresAt);
  }
}
