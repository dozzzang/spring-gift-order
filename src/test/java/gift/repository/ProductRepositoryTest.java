package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
public class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  void save() {
    Product expected = new Product("product", 10000, "thisisurl.com");
    Product actual = productRepository.save(expected);

    assertThat(actual.getId()).isNotNull();
    assertThat(actual.getName()).isEqualTo(expected.getName());
    assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl());
    assertThat(actual.isKakaoApproval()).isFalse();
  }

  @Test
  void findById() {
    Product savedProduct = productRepository.save(new Product("product", 10000, "thisisurl.com"));
    Long savedId = savedProduct.getId();

    Optional<Product> foundProduct = productRepository.findById(savedId);
    assertThat(foundProduct.isPresent()).isTrue();
    assertThat(foundProduct.get().getName()).isEqualTo(savedProduct.getName());
    assertThat(foundProduct.get().getPrice()).isEqualTo(savedProduct.getPrice());
    assertThat(foundProduct.get().getImageUrl()).isEqualTo(savedProduct.getImageUrl());
  }

  @Test
  void findById_NotFound() {
    Optional<Product> foundProduct = productRepository.findById(999L);
    assertThat(foundProduct.isPresent()).isFalse();
  }

  @Test
  void deleteById() {
    Product savedProduct = productRepository.save(new Product("product", 10000, "thisisurl.com"));
    Long savedId = savedProduct.getId();

    productRepository.deleteById(savedId);

    Optional<Product> foundProduct = productRepository.findById(savedId);
    assertThat(foundProduct.isPresent()).isFalse();
  }

  @Test
  void findAll() {
    productRepository.save(new Product("product", 10000, "thisisurl.com"));
    productRepository.save(new Product("product2", 20000, "thisisurl2.com"));

    List<Product> allProducts = productRepository.findAll();
    assertThat(allProducts.size()).isEqualTo(2);
  }

  @Test
  @Transactional
  void updateProduct() {
    Product savedProduct = productRepository.save(new Product("product", 10000, "thisisurl.com"));
    Long savedId = savedProduct.getId();

    savedProduct.setName("updatedProduct");
    savedProduct.setPrice(20000);
    savedProduct.setImageUrl("updateurl.com");

    entityManager.flush();
    entityManager.clear();

    Product result = productRepository.findById(savedId).orElseThrow();
    assertThat(result.getName()).isEqualTo("updatedProduct");
    assertThat(result.getPrice()).isEqualTo(20000);
    assertThat(result.getImageUrl()).isEqualTo("updateurl.com");
  }

  @Test
  void saveProductWithKakaoApproval() {
    Product expected = new Product("product", 10000, "thisisurl.com", true);
    Product actual = productRepository.save(expected);

    assertThat(actual.getId()).isNotNull();
    assertThat(actual.isKakaoApproval()).isTrue();
  }
}
