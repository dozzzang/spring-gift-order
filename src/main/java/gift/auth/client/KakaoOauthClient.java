package gift.auth.client;

import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.exception.ErrorCode;
import gift.exception.KakaoApiErrorException;
import gift.exception.KakaoClientErrorException;
import gift.exception.KakaoLoginErrorException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class KakaoOauthClient {

  private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
  private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
  private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
  private static final String KAKAO_MESSAGE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
  private static final Logger log = LoggerFactory.getLogger(KakaoOauthClient.class);

  private final String clientId;
  private final String redirectUrl;
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
    List<String> scopes = List.of("talk_message", "account_email");

    return UriComponentsBuilder
        .fromUriString(KAKAO_AUTH_URL)
        .path("/oauth/authorize")
        .queryParam("scope", String.join(",", scopes))
        .queryParam("response_type", "code")
        .queryParam("redirect_uri", redirectUrl)
        .queryParam("client_id", clientId)
        .encode()
        .toUriString();
  }

  public KakaoTokenResponseDto getAccessToken(String authorizationCode) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("redirect_uri", redirectUrl);
    body.add("code", authorizationCode);

    return executeKakaoApiRequest("POST", KAKAO_TOKEN_URL, null, body,
        KakaoTokenResponseDto.class, ErrorCode.KAKAO_LOGIN_ERROR);
  }

  public KakaoUserInfoDto getUserInfo(String accessToken) {
    return executeKakaoApiRequest("GET", KAKAO_USER_INFO_URL, accessToken, null,
        KakaoUserInfoDto.class, ErrorCode.KAKAO_API_ERROR);
  }

  public void sendKakaoTalkMessage(String accessToken, String templateObject) {
    final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("template_object", templateObject);

    executeKakaoApiRequest("POST", KAKAO_MESSAGE_URL, accessToken, body,
        Void.class, ErrorCode.KAKAO_API_ERROR);
  }

  public KakaoTokenResponseDto refreshAccessToken(String refreshToken) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "refresh_token");
    body.add("client_id", clientId);
    body.add("refresh_token", refreshToken);

    return executeKakaoApiRequest("POST", KAKAO_TOKEN_URL, null, body,
        KakaoTokenResponseDto.class, ErrorCode.KAKAO_LOGIN_ERROR);
  }

  private <T> T executeKakaoApiRequest(String method, String url, String accessToken,
      MultiValueMap<String, String> body,
      Class<T> responseType, ErrorCode serverErrorCode) {
    RestClient.RequestHeadersSpec<?> requestSpec;

    if ("GET".equals(method)) {
      requestSpec = restClient.get().uri(url);
    } else {
      requestSpec = restClient.post()
          .uri(url)
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(body);
    }

    if (accessToken != null) {
      requestSpec = requestSpec.header("Authorization", "Bearer " + accessToken);
    }

    RestClient.ResponseSpec responseSpec = requestSpec.retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
          log.error("카카오 토큰 요청실패로 4xx 에러 발생 상태 : {} Url : {}",
              response.getStatusCode(),url);
          throw new KakaoClientErrorException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          if (serverErrorCode == ErrorCode.KAKAO_LOGIN_ERROR) {
            log.error("카카오 로그인 실패로 5xx 에러 발생 상태 : {}, Url : {}",
                response.getStatusCode(),url);
            throw new KakaoLoginErrorException(serverErrorCode);
          } else {
            log.error("카카오 API 서버 에러 발생 상태 : {}, Url : {}",
                response.getStatusCode(), url);
            throw new KakaoApiErrorException(serverErrorCode);
          }
        });

    if (responseType == Void.class) {
      responseSpec.toBodilessEntity();
      return null;
    } else {
      return responseSpec.body(responseType);
    }
  }
}