package gift.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.product.dto.ProductResponseDto;
import gift.security.AdminInterceptor;
import gift.security.LoginUserArgumentResolver;
import gift.user.JwtTokenProvider;
import gift.user.entity.Role;
import gift.user.entity.User;
import gift.user.service.UserService;
import gift.wish.controller.WishRestController;
import gift.wish.dto.WishRequestDto;
import gift.wish.dto.WishResponseDto;
import gift.wish.service.WishService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WishRestController.class)
public class WishRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private WishService wishService;

  @MockitoBean
  private LoginUserArgumentResolver loginUserArgumentResolver;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private AdminInterceptor adminInterceptor;

  @MockitoBean
  private UserService userService;

  @BeforeEach
  void getToken() throws Exception {
    User mockUser = new User(1L, "admin@admin.com", "encodedPassword", Role.USER);
    given(loginUserArgumentResolver.supportsParameter(any())).willReturn(true);
    given(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
        .willReturn(mockUser);
  }

  @Test
  void 위시리스트에_상품을_추가할_수_있다() throws Exception {
    //given
    var token = "token";
    var request = new WishRequestDto(1L);
    var content = objectMapper.writeValueAsString(request);
    given(wishService.createWish(any(), any())).willReturn(new WishResponseDto(1L, 1L, 1L));

    // when
    var actual = createWish(token, content);

    // then
    assertThat(actual.getStatus()).isEqualTo(HttpStatus.CREATED.value());

    var response = objectMapper.readValue(actual.getContentAsString(), WishResponseDto.class);
    assertThat(response.productId()).isEqualTo(1L);
    assertThat(response.memberId()).isEqualTo(1L);
    assertThat(response.wishId()).isEqualTo(1L);
  }

  @Test
  void 위시리스트_상품_목록을_조회할_수_있다() throws Exception {
    // given
    var token = "token";
    List<ProductResponseDto> expectedProducts = List.of(
        new ProductResponseDto(1L, "상품1", 10000, "thisisurl", false, List.of())
    );
    given(wishService.getWishes(any())).willReturn(expectedProducts);

    // when
    var actual = getWishes(token);

    // then
    assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());

    var response = objectMapper.readValue(actual.getContentAsString(),
        objectMapper.getTypeFactory()
            .constructCollectionType(List.class, ProductResponseDto.class));
    assertThat(actual.getContentAsString())
        .contains("\"id\":1")
        .contains("\"name\":\"상품1\"");
  }

  @Test
  void 위시리스트에서_상품을_삭제할_수_있다() throws Exception {
    // given
    var token = "token";
    Long productId = 1L;
    doNothing().when(wishService).deleteWish(any(), any());

    // when
    var actual = deleteWish(token, productId);

    // then
    assertThat(actual.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  private MockHttpServletResponse createWish(String token, String content) throws Exception {
    return mockMvc.perform(
            post("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(content)
        )
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  private MockHttpServletResponse getWishes(String token) throws Exception {
    return mockMvc.perform(
            get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        )
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  private MockHttpServletResponse deleteWish(String token, Long productId) throws Exception {
    return mockMvc.perform(
            delete("/api/wishes/" + productId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        )
        .andDo(print())
        .andReturn()
        .getResponse();
  }
}