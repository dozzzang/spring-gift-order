package gift.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.product.controller.ProductApiController;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.service.ProductService;
import gift.security.AdminInterceptor;
import gift.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductApiController.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductService productService;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private AdminInterceptor adminInterceptor;

  @Test
  void 모든_조건_만족_정상실행() throws Exception {
    //given
    String requestBody = """
        {
        "name" : "평범한상품",
        "price" : 10000,
        "imageUrl" : "https://thisisurl.com",
        "options" : []
        }
        """;

    ProductResponseDto mockResponse = new ProductResponseDto(
        1L,
        "평범한상품",
        10000,
        "https://thisisurl.com",
        false,
        List.of()
    );

    when(productService.saveProduct(any(ProductRequestDto.class)))
        .thenReturn(mockResponse);

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("평범한상품"))
        .andExpect(jsonPath("$.price").value(10000))
        .andExpect(jsonPath("$.imageUrl").value("https://thisisurl.com"));


  }

  @Test
  void 상품명_15자초과_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "name" : "글자수가15자를초과하는상품명입니다",
        "price" : 10000,
        "imageUrl" : "https://thisisurl.com",
        "options" : []
        }
        """;

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("name"))
        .andExpect(jsonPath("$.errors[0].reason").value("상품명의 최대길이는 15자 입니다."));
  }

  @Test
  void 불허용_특수문자_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "name" : "$불허용특수문자상품$",
        "price" : 10000,
        "imageUrl" : "https://thisisurl.com",
        "options" : []
        }
        """;

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("name"))
        .andExpect(jsonPath("$.errors[0].reason").value("상품명에 (), [], +, -, &, /, _ 는 가능합니다."));
  }

  @Test
  void 카카오_포함_문구_예외발생() throws Exception {
    //given
    String requestBody = """
        {
        "name" : "카카오주식",
        "price" : 10000,
        "imageUrl" : "https://thisisurl.com",
        "options" : []
        }
        """;

    // Service에서 saveProduct() 메서드를 통해 비즈니스 로직 검증으로 Exception이 발생하는 것을 검증
    when(productService.saveProduct(any(ProductRequestDto.class)))
        .thenThrow(new BusinessException(ErrorCode.KAKAO_APPROVAL_REQUIRED));

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("KAKAO_APPROVAL_REQUIRED"))
        .andExpect(jsonPath("$.message").value("카카오 관련 상품명은 MD승인이 필요합니다."))
        .andExpect(jsonPath("$.errors").isEmpty());
  }


}
