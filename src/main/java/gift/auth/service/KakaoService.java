package gift.auth.service;

import gift.auth.client.KakaoOauthClient;
import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.auth.util.MessageTemplate;
import gift.exception.ErrorCode;
import gift.exception.KakaoLoginErrorException;
import gift.order.entity.Order;
import gift.user.JwtTokenProvider;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KakaoService {

  private static final Logger log = LoggerFactory.getLogger(KakaoService.class);
  private final KakaoTokenService kakaoTokenService;
  private final KakaoOauthClient kakaoOauthClient;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final MessageTemplate messageTemplate;

  public KakaoService(KakaoOauthClient kakaoOauthClient,
      UserRepository userRepository,
      JwtTokenProvider jwtTokenProvider,
      MessageTemplate messageTemplate,
      KakaoTokenService kakaoTokenService) {
    this.kakaoOauthClient = kakaoOauthClient;
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.messageTemplate = messageTemplate;
    this.kakaoTokenService = kakaoTokenService;
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

      kakaoTokenService.saveToken(user, tokenResponse);

      return jwtTokenProvider.generateToken(user);

    } catch (Exception e) {
      log.error("카카오 로그인 실패로 5xx 에러 발생 상태 : {}",
          e.getMessage());
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


  public void sendOrderMessage(Long userId, Order order) {
    String validAccessToken = kakaoTokenService.getValidAccessToken(userId);

    final String orderInfo = String.format(
        "상품: %s\n수량: %d개\n메시지: %s",
        order.getOption().getName(),
        order.getQuantity(),
        order.getMessage() != null ? order.getMessage() : "없음"
    );

    final String webUrl = "https://yourapp.com/orders/" + order.getId();
    final String templateObject = messageTemplate.createOrderMessage(orderInfo, webUrl);

    kakaoOauthClient.sendKakaoTalkMessage(validAccessToken, templateObject);
  }
}