package gift.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.exception.InvalidLoginException;
import gift.security.PasswordEncoder;
import gift.user.JwtTokenProvider;
import gift.user.controller.UserController;
import gift.user.dto.LoginRequestDto;
import gift.user.dto.LoginResponseDto;
import gift.user.dto.RegisterRequestDto;
import gift.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private PasswordEncoder passwordEncoder;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;


  @Test
  void 회원가입_성공() throws Exception {
    //given
    String requestBody = """
           {
          "email" : "admin@email.com",
          "password" : "Password123!"
        }
        """;

    doNothing().when(userService).registerUser(any(RegisterRequestDto.class));

    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated());
  }

  @Test
  void 로그인_성공() throws Exception {
    //given
    String requestBody = """
        {
        "email": "admin@email.com",
        "password": "password"
        }
        """;

    LoginResponseDto mockResponse = new LoginResponseDto("");

    when(userService.loginUser(any(LoginRequestDto.class))).thenReturn(mockResponse);

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(""));
  }

  @Test
  void 회원가입시_이메일_미기입_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "email": "",
        "password": "Password123!"
        }
        """;
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("email"));
  }

  @Test
  void 회원가입시_비밀번호_미기입_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "email": "admin@email.com",
        "password": ""
        }
        """;
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("password"));
  }

  @Test
  void 회원가입시_비밀번호_대문자없음_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "email": "admin@email.com",
        "password": "password123!"
        }
        """;
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("password"))
        .andExpect(jsonPath("$.errors[0].reason").value("비밀번호에는 반드시 1글자 이상의 대문자가 포함되어야 합니다."));
  }

  @Test
  void 회원가입시_비밀번호_숫자_3글자미만_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "email": "admin@email.com",
        "password": "Password12!"
        }
        """;
    mockMvc.perform(post("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("password"))
        .andExpect(jsonPath("$.errors[0].reason").value("비밀번호에는 반드시 숫자가 3개 이상 포함되어야 합니다."));
  }

  @Test
  void 회원가입시_비밀번호_특수문자없음_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "email": "admin@email.com",
        "password": "Password123"
        }
        """;
    mockMvc.perform(
            post("/api/users/register").
                contentType(MediaType.APPLICATION_JSON).
                content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("password"))
        .andExpect(jsonPath("$.errors[0].reason").value("비밀번호에는 반드시 !,@,#,$,. 중 하나 이상의 특수문자가 포함되어야 합니다."));
  }


  @Test
  void 로그인시_사용자없음_예외발생() throws Exception {
    // given
    String requestBody = """
        {
            "email": "unknown@email.com",
            "password": "password"
        }
        """;

    when(userService.loginUser(any(LoginRequestDto.class))).thenThrow(new InvalidLoginException());

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("INVALID_LOGIN"))
        .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 일치하지 않습니다."))
        .andExpect(jsonPath("$.errors").isEmpty());
  }


}

