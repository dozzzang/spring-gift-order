package gift.auth.controller;

import gift.auth.service.KakaoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KakaoController {

  private final KakaoService kakaoService;

  public KakaoController(KakaoService kakaoService) {
    this.kakaoService = kakaoService;
  }


  @GetMapping("/kakao/login")
  @ResponseBody
  public ResponseEntity<Map<String, String>> getKakaoLoginUrl() {
    String loginUrl = kakaoService.getKakaoLoginUrl();
    return ResponseEntity.ok(Map.of("loginUrl", loginUrl));
  }

  @GetMapping(value = "/", params = "code")
  public String handleKakaoCallback(@RequestParam String code,
      HttpServletResponse response) {
      try {
        String jwtToken = kakaoService.processKakaoLogin(code);

        Cookie jwtCookie = new Cookie("accessToken", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 30);
        response.addCookie(jwtCookie);

        return "redirect:/";

      } catch (Exception e) {
        return "redirect:/users/login";
      }
    }
}