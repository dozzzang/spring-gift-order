package gift.auth.service;

import gift.auth.client.KakaoOauthClient;
import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.exception.ErrorCode;
import gift.exception.KakaoLoginErrorException;
import gift.user.JwtTokenProvider;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KakaoService {

  private final KakaoOauthClient kakaoOauthClient;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public KakaoService(KakaoOauthClient kakaoOauthClient,
      UserRepository userRepository,
      JwtTokenProvider jwtTokenProvider) {
    this.kakaoOauthClient = kakaoOauthClient;
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public String getKakaoLoginUrl() {
    return kakaoOauthClient.getKakaoLoginUrl();
  }

  @Transactional
  public String processKakaoLogin(String authorizationCode) {
    try {
      KakaoTokenResponseDto tokenResponse = kakaoOauthClient.getAccessToken(authorizationCode);
      KakaoUserInfoDto userInfo = kakaoOauthClient.getUserInfo(tokenResponse.accessToken());
      User user = findOrCreateUser(userInfo);
      return jwtTokenProvider.generateToken(user);

    } catch (Exception e) {
      throw new KakaoLoginErrorException(ErrorCode.KAKAO_LOGIN_ERROR);
    }
  }

  private User findOrCreateUser(KakaoUserInfoDto userInfo) {
    String email = userInfo.getEmailSafely();
    String kakaoId = userInfo.id().toString();

    return userRepository.findByEmail(email)
        .orElseGet(() -> createNewKakaoUser(email, kakaoId));
  }


  private User createNewKakaoUser(String email, String kakaoId) {
    String dummyPassword = "KAKAO_LOGIN_" + kakaoId;
    User newUser = new User(email, dummyPassword);

    return userRepository.save(newUser);
  }
}