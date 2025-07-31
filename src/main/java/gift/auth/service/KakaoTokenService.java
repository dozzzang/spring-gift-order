package gift.auth.service;

import gift.auth.client.KakaoOauthClient;
import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.entity.UserKakaoToken;
import gift.auth.repository.UserKakaoTokenRepository;
import gift.exception.ErrorCode;
import gift.exception.ReauthorizeRequiredException;
import gift.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KakaoTokenService {

  private final UserKakaoTokenRepository userKakaoTokenRepository;
  private final KakaoOauthClient kakaoOauthClient;

  public KakaoTokenService(UserKakaoTokenRepository userKakaoTokenRepository
  , KakaoOauthClient kakaoOauthClient) {
    this.userKakaoTokenRepository = userKakaoTokenRepository;
    this.kakaoOauthClient = kakaoOauthClient;
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

  @Transactional
  public String getValidAccessToken(Long userId) {
    UserKakaoToken token = userKakaoTokenRepository.findByUserId(userId)
        .orElseThrow(() -> new ReauthorizeRequiredException(ErrorCode.REAUTHORIZED_REQUIRED_ERROR));

    if (!token.isAccessTokenExpired()) {
      return token.getAccessToken();
    }

    if (token.isRefreshTokenExpired()) {
      throw new ReauthorizeRequiredException(ErrorCode.REAUTHORIZED_REQUIRED_ERROR);
    }

    return refreshAndReturnAccessToken(token);
  }

  private String refreshAndReturnAccessToken(UserKakaoToken token) {
    try {
      KakaoTokenResponseDto refreshResponse = kakaoOauthClient.refreshAccessToken(token.getRefreshToken());

      token.updateTokens(
          refreshResponse.accessToken(),
          refreshResponse.refreshToken() != null ? refreshResponse.refreshToken() : token.getRefreshToken(),
          refreshResponse.expiresIn(),
          refreshResponse.refreshTokenExpiresIn() != null ? refreshResponse.refreshTokenExpiresIn() : 86400 * 30 // 기본값 30일
      );

      return refreshResponse.accessToken();

    } catch (Exception e) {
      throw new ReauthorizeRequiredException(ErrorCode.REAUTHORIZED_REQUIRED_ERROR);
    }
  }
}