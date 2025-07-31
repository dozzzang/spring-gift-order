package gift.auth.client;

import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.exception.ErrorCode;
import gift.exception.KakaoApiErrorException;
import gift.exception.KakaoClientErrorException;
import gift.exception.KakaoLoginErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoOauthClient {

  private final String clientId;
  private final String redirectUrl;

  private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
  private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
  private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
  private static final String KAKAO_MESSAGE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

  private final RestClient restClient;

  public KakaoOauthClient(@Value("${kakao.client.id}") String clientId,
      @Value("${kakao.redirect.url}") String redirectUrl) {
    this.clientId = clientId;
    this.redirectUrl = redirectUrl;

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(5000);
    requestFactory.setReadTimeout(5000);
    requestFactory.setConnectionRequestTimeout(5000);

    this.restClient = RestClient.builder()
        .requestFactory(requestFactory)
        .build();
  }

  public String getKakaoLoginUrl() {
    return KAKAO_AUTH_URL + "/oauth/authorize" +
        "?scope=talk_message,account_email" +
        "&response_type=code" +
        "&redirect_uri=" + redirectUrl +
        "&client_id=" + clientId;
  }

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
          throw new KakaoClientErrorException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, responseEntity) -> {
          throw new KakaoLoginErrorException(ErrorCode.KAKAO_LOGIN_ERROR);
        })
        .body(KakaoTokenResponseDto.class);
  }

  public KakaoUserInfoDto getUserInfo(String accessToken) {
    return restClient.get()
        .uri(KAKAO_USER_INFO_URL)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
          throw new KakaoClientErrorException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          throw new KakaoApiErrorException(ErrorCode.KAKAO_API_ERROR);
        })
        .body(KakaoUserInfoDto.class);
  }

  public void sendKakaoTalkMessage(String accessToken,String templateObject) {
    final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("template_object", templateObject);

    restClient.post()
        .uri(KAKAO_MESSAGE_URL)
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
          throw new KakaoClientErrorException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          throw new KakaoApiErrorException(ErrorCode.KAKAO_API_ERROR);
        })
        .toBodilessEntity();
  }
  public KakaoTokenResponseDto refreshAccessToken(String refreshToken) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "refresh_token");
    body.add("client_id", clientId);
    body.add("refresh_token", refreshToken);

    return restClient.post()
        .uri(KAKAO_TOKEN_URL)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(body)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, responseEntity) -> {
          throw new KakaoClientErrorException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, responseEntity) -> {
          throw new KakaoLoginErrorException(ErrorCode.KAKAO_LOGIN_ERROR);
        })
        .body(KakaoTokenResponseDto.class);
  }
}