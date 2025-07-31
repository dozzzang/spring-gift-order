package gift.controller;

import gift.auth.controller.KakaoController;
import gift.auth.service.KakaoService;
import gift.security.AdminInterceptor;
import gift.security.LoginUserArgumentResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KakaoController.class)
class KakaoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private KakaoService kakaoService;

  @MockitoBean
  private AdminInterceptor adminInterceptor;

  @MockitoBean
  private LoginUserArgumentResolver loginUserArgumentResolver;

  @Test
  void 카카오_로그인_URL_조회_성공() throws Exception {
    // Given
    String expectedUrl = "https://kauth.kakao.com/oauth/authorize?scope=talk_message,account_email&response_type=code&redirect_uri=http://localhost:8080&client_id=test-id";
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
    String jwtToken = "jwt.test.token";

    given(kakaoService.processKakaoLogin(code)).willReturn(jwtToken);

    // When & Then
    mockMvc.perform(get("/")
            .param("code", code))
        .andExpect(redirectedUrl("/"))
        .andExpect(cookie().value("accessToken", jwtToken))
        .andExpect(cookie().path("accessToken", "/"));
  }

  @Test
  void 카카오_콜백_처리_실패시_로그인_페이지로_리다이렉트() throws Exception {
    // Given
    String invalidCode = "invalidCode";
    willThrow(new RuntimeException("카카오 로그인 실패"))
        .given(kakaoService).processKakaoLogin(invalidCode);

    // When & Then
    mockMvc.perform(get("/")
            .param("code", invalidCode))
        .andExpect(redirectedUrl("/users/login"));
  }
}