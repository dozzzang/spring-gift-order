package gift.auth.controller;


import static gift.exception.ErrorCode.INTERNAL_SERVER_ERROR;

import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.auth.service.KakaoService;
import gift.exception.InternalServerException;
import gift.user.JwtTokenProvider;
import gift.user.entity.User;
import gift.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class KakaoController {

  private final KakaoService kakaoService;
  private final UserService userService;

  public KakaoController(KakaoService kakaoService, UserService userService) {
    this.kakaoService = kakaoService;
    this.userService = userService;
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
        KakaoTokenResponseDto kakaoTokenResponseDto = kakaoService.getAccessToken(code);

        KakaoUserInfoDto kaKaoUserInfoDto = kakaoService.getUserInfo(
            kakaoTokenResponseDto.accessToken());

        String jwtToken = userService.handleKaKaoLogin(kaKaoUserInfoDto.getEmailSafely(),
            kaKaoUserInfoDto.id().toString());

        Cookie jwtCookie = new Cookie("accessToken", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 30);
        response.addCookie(jwtCookie);

        return "redirect:/";

      } catch (Exception e) {
        return new InternalServerException(INTERNAL_SERVER_ERROR).getMessage();
      }
    }
}