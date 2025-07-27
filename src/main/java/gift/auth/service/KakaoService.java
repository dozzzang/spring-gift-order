package gift.auth.service;


import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {

  @Value("${kakao.client.id}")
  private String clientId;

  @Value("${kakao.redirect.url}")
  private String redirectUrl;

  private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
  private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

  private final RestClient restClient;

  public KakaoService() {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

    requestFactory.setConnectTimeout(5000);
    requestFactory.setReadTimeout(5000);
    requestFactory.setConnectionRequestTimeout(5000);

    this.restClient = RestClient.builder()
        .requestFactory(requestFactory)
        .build();
  }

  // Step 1: 카카오 로그인 URL 생성 사용자를 카카오 인증 서버로 리다이렉트하기 위한 URL
  public String getKakaoLoginUrl() {
    return KAKAO_AUTH_URL + "/oauth/authorize" +
        "?scope=talk_message" +
        "&response_type=code" +
        "&redirect_uri=" + redirectUrl +
        "&client_id=" + clientId;
  }


  //Step2: 인가 코드로 액세스 토큰 받기

  public KakaoTokenResponseDto getAccessToken(String authorizationCode) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("redirect_uri", redirectUrl);
    body.add("code", authorizationCode);

    return restClient.post()
        .uri(KAKAO_TOKEN_URL)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, responseEntity) -> {
          throw new RuntimeException("토큰 요청 실패 - 4xx 오류. 카카오톡 메시지 전송 권한을 확인하세요.");
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, responseEntity) -> {
          throw new RuntimeException("카카오 서버 오류 - 5xx 오류");
        })
        .body(KakaoTokenResponseDto.class);
  }

  public KakaoUserInfoDto getUserInfo(String accessToken) {
    return restClient.get()
        .uri("https://kapi.kakao.com/v2/user/me")
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
          throw new RuntimeException("사용자 정보 요청 실패 - 4xx 오류");
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          throw new RuntimeException("카카오 서버 오류 - 5xx 오류");
        })
        .body(KakaoUserInfoDto.class);
  }
}