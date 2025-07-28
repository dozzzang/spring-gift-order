package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.user.entity.User;
import gift.user.entity.Role;
import gift.user.repository.UserRepository;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishRepositoryTest {

  @Autowired
  private WishRepository wishRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  void save() {
    User user = new User("user@user.com", "password");
    User savedUser = userRepository.save(user);

    Product product = new Product("product", 10000, "thisisurl.com");
    Product savedProduct = productRepository.save(product);

    Wish expected = new Wish(savedUser, savedProduct);
    Wish actual = wishRepository.save(expected);

    assertThat(actual.getId()).isNotNull();
    assertThat(actual.getUser().getId()).isEqualTo(savedUser.getId());
    assertThat(actual.getProduct().getId()).isEqualTo(savedProduct.getId());
  }

  @Test
  void findByUserId() {
    User user = new User("user@user.com", "password");
    User savedUser = userRepository.save(user);

    Product product1 = new Product("product", 10000, "thisisurl.com");
    Product savedProduct1 = productRepository.save(product1);

    Product product2 = new Product("product2", 20000, "thisisurl2.com");
    Product savedProduct2 = productRepository.save(product2);

    wishRepository.save(new Wish(savedUser, savedProduct1));
    wishRepository.save(new Wish(savedUser, savedProduct2));

    List<Wish> userWishes = wishRepository.findByUserId(savedUser.getId());

    assertThat(userWishes.size()).isEqualTo(2);
  }

  @Test
  void findByUserIdAndProductId() {
    User user = new User("user@user.com", "password");
    User savedUser = userRepository.save(user);

    Product product = new Product("product", 10000, "thisisurl.com");
    Product savedProduct = productRepository.save(product);

    Wish savedWish = wishRepository.save(new Wish(savedUser, savedProduct));

    Optional<Wish> foundWish = wishRepository.findByUserIdAndProductId(
        savedUser.getId(), savedProduct.getId());

    assertThat(foundWish.isPresent()).isTrue();
    assertThat(foundWish.get().getId()).isEqualTo(savedWish.getId());
  }

  @Test
  void findByUserIdAndProductId_NotFound() {
    Optional<Wish> foundWish = wishRepository.findByUserIdAndProductId(77777L, 77777L);

    assertThat(foundWish.isPresent()).isFalse();
  }


  @Test
  void deleteByUserIdAndProductId() {
    User user = new User("user@user.com", "password");
    User savedUser = userRepository.save(user);

    Product product = new Product("product", 10000, "thisisurl.com");
    Product savedProduct = productRepository.save(product);

    wishRepository.save(new Wish(savedUser, savedProduct));

    wishRepository.deleteByUserIdAndProductId(savedUser.getId(), savedProduct.getId());

    Optional<Wish> deletedWish = wishRepository.findByUserIdAndProductId(
        savedUser.getId(), savedProduct.getId());
    assertThat(deletedWish.isPresent()).isFalse();
  }
}