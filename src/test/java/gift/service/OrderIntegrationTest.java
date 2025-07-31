package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.client.KakaoOauthClient;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.user.JwtTokenProvider;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class OrderIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OptionRepository optionRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private WishRepository wishRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private KakaoOauthClient kakaoOauthClient;

  private MockMvc mockMvc;
  private User testUser;
  private Product testProduct;
  private Option testOption;
  private String jwtToken;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    testUser = new User("test@example.com", "password123");
    testUser = userRepository.save(testUser);

    testProduct = new Product("테스트 피자", 15000,  "pizza.jpg");
    testProduct = productRepository.save(testProduct);

    testOption = new Option("라지 사이즈", 10, testProduct);
    testOption = optionRepository.save(testOption);

    Wish wish = new Wish(testUser, testProduct);
    wishRepository.save(wish);

    jwtToken = jwtTokenProvider.generateToken(testUser);
  }

  @Test
  void 주문_성공() throws Exception {
    // given
    String requestBody = objectMapper.writeValueAsString(Map.of(
        "optionId", testOption.getId(),
        "quantity", 3,
        "message", "빠른 배송 부탁드립니다"
    ));

    // when & then
    mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.optionId").value(testOption.getId()))
        .andExpect(jsonPath("$.quantity").value(3))
        .andExpect(jsonPath("$.message").value("빠른 배송 부탁드립니다"))
        .andExpect(jsonPath("$.orderDateTime").exists());

    // 재고 차감
    Option updatedOption = optionRepository.findById(testOption.getId()).orElseThrow();
    assertThat(updatedOption.getQuantity()).isEqualTo(7);

    // 주문 생성
    Order createdOrder = orderRepository.findByUser(testUser).get(0);
    assertThat(createdOrder.getUser()).isEqualTo(testUser);
    assertThat(createdOrder.getOption()).isEqualTo(testOption);
    assertThat(createdOrder.getQuantity()).isEqualTo(3);
    assertThat(createdOrder.getMessage()).isEqualTo("빠른 배송 부탁드립니다");

    // 위시리스트 삭제
    boolean wishExists = wishRepository.existsByUserIdAndProductId(testUser.getId(), testProduct.getId());
    assertThat(wishExists).isFalse();
  }

  @Test
  void 주문생성_재고부족() throws Exception {
    // given
    String requestBody = objectMapper.writeValueAsString(Map.of(
        "optionId", testOption.getId(),
        "quantity", 15, // 재고 : 10 / 주문 : 15
        "message", "재고 부족 테스트"
    ));

    // when & then
    mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());

    Option unchangedOption = optionRepository.findById(testOption.getId()).orElseThrow();
    assertThat(unchangedOption.getQuantity()).isEqualTo(10);
    assertThat(orderRepository.findByUser(testUser)).isEmpty();
  }

  @Test
  void 주문생성_수량0_실패() throws Exception {
    // given
    String requestBody = objectMapper.writeValueAsString(Map.of(
        "optionId", testOption.getId(),
        "quantity", 0,
        "message", "수량 검증 테스트"
    ));

    // when & then
    mockMvc.perform(post("/api/orders")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());
  }
}