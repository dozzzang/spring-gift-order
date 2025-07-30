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

  protected UserKakaoToken() {
  }
}
