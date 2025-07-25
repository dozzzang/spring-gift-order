package gift.auth.controller;

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
  void 인가_코드로_토큰_발급_성공() throws Exception {
    // Given
    String authCode = "valid-auth-code";
    KakaoTokenResponseDto tokenResponse = new KakaoTokenResponseDto(
        "bearer",
        "KakaoAK.test-access-token",
        21599
    );

    given(kakaoService.getAccessToken(authCode)).willReturn(tokenResponse);

    // When & Then
    mockMvc.perform(post("/kakao/token")
            .param("code", authCode))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("토큰 발급 성공"))
        .andExpect(jsonPath("$.tokenType").value("bearer"))
        .andExpect(jsonPath("$.accessToken").value("KakaoAK.test-access-token"))
        .andExpect(jsonPath("$.expiresIn").value(21599));
  }

  @Test
  void 잘못된_인가_코드로_토큰_요청시_에러_응답() throws Exception {
    // Given
    String invalidCode = "strangeCode";
    willThrow(new RuntimeException("토큰 요청 실패"))
        .given(kakaoService).getAccessToken(invalidCode);

    // When & Then
    mockMvc.perform(post("/kakao/token")
            .param("code", invalidCode))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("토큰 요청 실패"))
        .andExpect(jsonPath("$.message").value("토큰 요청 실패"));
  }

  @Test
  void 액세스_토큰으로_사용자_정보_조회_성공() throws Exception {
    // Given
    String accessToken = "valid-access-token";
    KakaoUserInfoDto userInfo = new KakaoUserInfoDto(
        1L,
        new KakaoUserInfoDto.KakaoAccount("test@dohyun.com")
    );
    given(kakaoService.getUserInfo(accessToken)).willReturn(userInfo);

    // When & Then
    mockMvc.perform(post("/kakao/userinfo")
            .param("accessToken", accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("사용자 정보 조회 성공"))
        .andExpect(jsonPath("$.kakaoId").value(1))
        .andExpect(jsonPath("$.email").value("test@dohyun.com"));
  }

  @Test
  void 만료된_액세스_토큰으로_사용자_정보_조회시_에러_응답() throws Exception {
    // Given
    String token = "expiredAccessToken";
    willThrow(new RuntimeException("사용자 정보 요청 실패"))
        .given(kakaoService).getUserInfo(token);

    // When & Then
    mockMvc.perform(post("/kakao/userinfo")
            .param("accessToken", token))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("사용자 정보 요청 실패"))
        .andExpect(jsonPath("$.message").value("사용자 정보 요청 실패"));
  }
}