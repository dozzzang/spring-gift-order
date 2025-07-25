package gift.auth.controller;


import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.auth.service.KakaoService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoController {

  private final KakaoService kakaoService;

  public KakaoController(KakaoService kakaoService) {
    this.kakaoService = kakaoService;
  }


  @GetMapping("/kakao/login")
  public ResponseEntity<Map<String, String>> getKakaoLoginUrl() {
    String loginUrl = kakaoService.getKakaoLoginUrl();
    return ResponseEntity.ok(Map.of("loginUrl", loginUrl));
  }

  @PostMapping("/kakao/token")
  public ResponseEntity<Map<String, Object>> getToken
      (@RequestParam String code) {
    try {
      KakaoTokenResponseDto tokenResponse = kakaoService.getAccessToken(code);

      return ResponseEntity.ok(Map.of(
          "message", "토큰 발급 성공",
          "tokenType", tokenResponse.tokenType(),
          "accessToken", tokenResponse.accessToken(),
          "expiresIn", tokenResponse.expiresIn()
      ));

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
          "error", "토큰 요청 실패",
          "message", e.getMessage()
      ));
    }
  }

  @PostMapping("/kakao/userinfo")
  public ResponseEntity<Map<String, Object>> getUserInfo(@RequestParam String accessToken) {
    try {
      KakaoUserInfoDto userInfo = kakaoService.getUserInfo(accessToken);

      return ResponseEntity.ok(Map.of(
          "message", "사용자 정보 조회 성공",
          "kakaoId", userInfo.id(),
          "email", userInfo.getEmailSafely()
      ));

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of(
          "error", "사용자 정보 요청 실패",
          "message", e.getMessage()
      ));
    }
  }

}