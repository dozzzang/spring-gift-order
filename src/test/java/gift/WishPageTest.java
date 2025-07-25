package gift;

import static gift.user.entity.Role.USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@SpringBootTest
public class WishPageTest {

  @Autowired
  private WishRepository wishRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(new User("admin@admin.com", "password"));

    Product product1 = productRepository.save(new Product("product1", 10000, "thisisurl1"));
    Product product2 = productRepository.save(new Product("product2", 10000, "thisisurl2"));
    Product product3 = productRepository.save(new Product("product3", 10000, "thisisurl3"));
    Product product4 = productRepository.save(new Product("product4", 10000, "thisisurl4"));
    Product product5 = productRepository.save(new Product("product5", 10000, "thisisurl5"));
    Product product6 = productRepository.save(new Product("product6", 10000, "thisisurl6"));
    Product product7 = productRepository.save(new Product("product7", 10000, "thisisurl7"));
    Product product8 = productRepository.save(new Product("product8", 10000, "thisisurl8"));

    wishRepository.save(new Wish(user, product1));
    wishRepository.save(new Wish(user, product2));
    wishRepository.save(new Wish(user, product3));
    wishRepository.save(new Wish(user, product4));
    wishRepository.save(new Wish(user, product5));
    wishRepository.save(new Wish(user, product6));
    wishRepository.save(new Wish(user, product7));
    wishRepository.save(new Wish(user, product8));
  }

  @Test
  void 위시리스트_통합테스트() throws Exception {
    PageRequest pageRequest = PageRequest.of(0, 7, Sort.by(Direction.DESC, "id"));
    Page<Wish> wishPage = wishRepository.findByUserId(user.getId(), pageRequest);

    Page<ProductRequestDto> toMap = wishPage.map(
        wish -> new ProductRequestDto(wish.getProduct().getName(),
            wish.getProduct().getPrice(),
            wish.getProduct().getImageUrl()));

    List<ProductRequestDto> content = toMap.getContent();

    assertThat(content.size()).isEqualTo(7);
    assertThat(toMap.getTotalElements()).isEqualTo(8);
    assertThat(toMap.getNumber()).isEqualTo(0);
    assertThat(toMap.getTotalPages()).isEqualTo(2);
    assertThat(toMap.isFirst()).isTrue();
    assertThat(toMap.hasNext()).isTrue();

    assertThat(content.get(0).name()).isEqualTo("product8");
    assertThat(content.get(1).name()).isEqualTo("product7");
    assertThat(content.get(2).name()).isEqualTo("product6");
  }

}
