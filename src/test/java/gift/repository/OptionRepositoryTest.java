package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OptionRepositoryTest {

  @Autowired
  private OptionRepository optionRepository;

  @Autowired
  private ProductRepository productRepository;

  private Product product;

  @BeforeEach
  void setUp() {
    product = new Product("맥북 프로", 2500000, "macbook-pro.url");
    productRepository.save(product);
  }

  @Test
  void 상품_ID로_옵션_목록_조회() {
    // given
    Option option1 = new Option("13인치 스페이스 그레이", 30);
    Option option2 = new Option("15인치 실버", 20);

    product.addOption(option1);
    product.addOption(option2);

    optionRepository.save(option1);
    optionRepository.save(option2);

    // when
    List<Option> options = optionRepository.findByProductId(product.getId());

    // then
    assertThat(options).hasSize(2);
    assertThat(options)
        .extracting(Option::getName)
        .containsExactlyInAnyOrder("13인치 스페이스 그레이", "15인치 실버");
  }

  @Test
  void 상품_ID_옵션명_중복_체크() {
    // given
    Option option = new Option("13인치 미드나이트", 25);
    product.addOption(option);
    optionRepository.save(option);

    // when & then
    assertThat(optionRepository.existsByProductIdAndName(product.getId(), "13인치 미드나이트"))
        .isTrue();
    assertThat(optionRepository.existsByProductIdAndName(product.getId(), "15인치 골드"))
        .isFalse();
  }

  @Test
  void 존재하지_않는_상품_ID_조회_빈_리스트를_반환() {
    // given
    Long nonExistentProductId = 999L;

    // when
    List<Option> options = optionRepository.findByProductId(nonExistentProductId);

    // then
    assertThat(options).isEmpty();
  }

  @Test
  void 상품_연관관계_정상_설정() {
    // given
    Option option = new Option("15인치 스페이스 블랙", 15);
    product.addOption(option);

    // when
    Option savedOption = optionRepository.save(option);

    // then
    assertThat(savedOption.getId()).isNotNull();
    assertThat(savedOption.getProduct()).isEqualTo(product);
    assertThat(savedOption.getProduct().getId()).isEqualTo(product.getId());
  }
}