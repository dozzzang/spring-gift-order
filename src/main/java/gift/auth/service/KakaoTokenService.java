package gift.auth.service;

import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.entity.UserKakaoToken;
import gift.auth.repository.UserKakaoTokenRepository;
import gift.user.entity.User;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KakaoTokenService {

  private final UserKakaoTokenRepository userKakaoTokenRepository;

  public KakaoTokenService(UserKakaoTokenRepository userKakaoTokenRepository) {
    this.userKakaoTokenRepository = userKakaoTokenRepository;
  }

  @Transactional
  public void saveToken(User user, KakaoTokenResponseDto tokenResponse) {
    userKakaoTokenRepository.findByUserId(user.getId())
        .ifPresentOrElse(
            existingToken -> updateExistingToken(existingToken, tokenResponse),
            () -> createToken(user, tokenResponse)
        );
  }

  private void updateExistingToken(UserKakaoToken existingToken, KakaoTokenResponseDto tokenResponse) {
    existingToken.updateTokens(
        tokenResponse.accessToken(),
        tokenResponse.refreshToken(),
        tokenResponse.expiresIn(),
        tokenResponse.refreshTokenExpiresIn()
    );
  }

  private void createToken(User user, KakaoTokenResponseDto tokenResponse) {
    UserKakaoToken newToken = UserKakaoToken.create(
        user,
        tokenResponse.accessToken(),
        tokenResponse.refreshToken(),
        tokenResponse.expiresIn(),
        tokenResponse.refreshTokenExpiresIn()
    );
    userKakaoTokenRepository.save(newToken);
  }
}