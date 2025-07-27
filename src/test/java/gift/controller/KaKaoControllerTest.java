package gift.controller;

import gift.auth.controller.KakaoController;
import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.dto.KakaoUserInfoDto;
import gift.auth.service.KakaoService;
import gift.security.AdminInterceptor;
import gift.security.LoginUserArgumentResolver;
import gift.user.JwtTokenProvider;
import gift.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KakaoController.class)
class KakaoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private AdminInterceptor adminInterceptor;

  @MockitoBean
  private LoginUserArgumentResolver loginUserArgumentResolver;

  @MockitoBean
  private KakaoService kakaoService;

  @Test
  void 카카오_로그인_URL_조회_성공() throws Exception {
    // Given
    String expectedUrl = "https://kauth.kakao.com/oauth/authorize?scope=talk_message&response_type=code&redirect_uri=http://localhost:8080&client_id=test-id";
    given(kakaoService.getKakaoLoginUrl()).willReturn(expectedUrl);

    // When & Then
    mockMvc.perform(get("/kakao/login"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.loginUrl").value(expectedUrl));
  }

  @Test
  void 카카오_콜백_처리_성공() throws Exception {
    // Given
    String code = "validCode";

    KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
        "bearer",
        "KakaoAK.test-access-token",
        21599
    );

    KakaoUserInfoDto userInfo = new KakaoUserInfoDto(
        1L,
        new KakaoUserInfoDto.KakaoAccount("test@dohyun.com")
    );

    String jwtToken = "jwt.test.token";

    given(kakaoService.getAccessToken(code)).willReturn(tokenResponse);
    given(kakaoService.getUserInfo(tokenResponse.accessToken())).willReturn(userInfo);
    given(userService.handleKaKaoLogin(userInfo.getEmailSafely(), userInfo.id().toString()))
        .willReturn(jwtToken);

    // When & Then
    mockMvc.perform(get("/")
            .param("code", code))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"))
        .andExpect(cookie().value("accessToken", jwtToken))
        .andExpect(cookie().path("accessToken", "/"));
  }

  @Test
  void 카카오_콜백_처리_실패시_로그인_페이지로_리다이렉트() throws Exception {
    // Given
    String invalidCode = "invalidCode";
    willThrow(new RuntimeException("토큰 요청 실패"))
        .given(kakaoService).getAccessToken(invalidCode);

    // When & Then
    mockMvc.perform(get("/")
            .param("code", invalidCode))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/login"));
  }

  @Test
  void 카카오_사용자_정보_조회_실패시_로그인_페이지로_리다이렉트() throws Exception {
    // Given
    String code = "validCode";
    String accessToken = "valid-token";

    KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
        "bearer",
        accessToken,
        21599
    );

    given(kakaoService.getAccessToken(code)).willReturn(tokenResponse);
    willThrow(new RuntimeException("사용자 정보 조회 실패"))
        .given(kakaoService).getUserInfo(accessToken);

    // When & Then
    mockMvc.perform(get("/")
            .param("code", code))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/login"));
  }

  @Test
  void JWT_토큰_생성_실패시_로그인_페이지로_리다이렉트() throws Exception {
    // Given
    String code = "validCode";

    KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
        "bearer",
        "valid-token",
        21599
    );

    KakaoUserInfoDto userInfo = new KakaoUserInfoDto(
        1L,
        new KakaoUserInfoDto.KakaoAccount("test@dohyun.com")
    );

    given(kakaoService.getAccessToken(code)).willReturn(tokenResponse);
    given(kakaoService.getUserInfo(tokenResponse.accessToken())).willReturn(userInfo);
    willThrow(new RuntimeException("JWT 생성 실패"))
        .given(userService).handleKaKaoLogin(userInfo.getEmailSafely(), userInfo.id().toString());

    // When & Then
    mockMvc.perform(get("/")
            .param("code", code))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/login"));
  }
}