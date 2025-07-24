package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.exception.OverlappingOptionNameException;
import gift.exception.ProductNotFoundException;
import gift.option.dto.OptionRequestDto;
import gift.option.dto.OptionResponseDto;
import gift.option.entity.Option;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductIntegrationTest {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRepository productRepository;

  @Test
  void 상품과_옵션_저장() {
    // given
    List<OptionRequestDto> optionDtos = List.of(
        new OptionRequestDto("13인치 스페이스 그레이", 30),
        new OptionRequestDto("15인치 실버", 20)
    );

    ProductRequestDto request = new ProductRequestDto(
        "맥북 프로", 2500000, "macbook-pro.url", optionDtos);

    // when
    ProductResponseDto response = productService.saveProduct(request);

    // then
    assertThat(response.options()).hasSize(2);
  }

  @Test
  void 중복된_옵션명_상품_저장시_예외_발생() {
    // given
    List<OptionRequestDto> options = List.of(
        new OptionRequestDto("13인치 미드나이트", 30),
        new OptionRequestDto("13인치 미드나이트", 20)
    );
    ProductRequestDto request = new ProductRequestDto(
        "맥북 에어", 1500000, "macbook-air.url", options);

    // when & then
    assertThatThrownBy(() -> productService.saveProduct(request))
        .isInstanceOf(OverlappingOptionNameException.class);
  }

  @Test
  void 상품_옵션_모두_수정() {
    // given
    List<OptionRequestDto> initialOptionDtos = List.of(
        new OptionRequestDto("13인치 초기 옵션", 10)
    );

    ProductRequestDto initialRequest = new ProductRequestDto(
        "초기 노트북", 1000000, "initial.url", initialOptionDtos);
    ProductResponseDto initialResponse = productService.saveProduct(initialRequest);
    Long productId = initialResponse.id();

    List<OptionRequestDto> updatedOptionDtos = List.of(
        new OptionRequestDto("13인치 스페이스 블랙", 25),
        new OptionRequestDto("15인치 골드", 15)
    );

    ProductRequestDto updateRequest = new ProductRequestDto(
        "수정된 맥북", 2000000, "updatedMacbook.url", updatedOptionDtos);

    // when
    ProductResponseDto updateResponse = productService.updateProduct(productId, updateRequest);

    // then
    assertThat(updateResponse.name()).isEqualTo("수정된 맥북");
    assertThat(updateResponse.price()).isEqualTo(2000000);
    assertThat(updateResponse.options()).hasSize(2);

    Product updatedProduct = productRepository.findById(productId).orElseThrow();
    assertThat(updatedProduct.getName()).isEqualTo("수정된 맥북");
    assertThat(updatedProduct.getOptions()).hasSize(2);
    assertThat(updatedProduct.getOptions())
        .extracting(Option::getName)
        .containsExactlyInAnyOrder("13인치 스페이스 블랙", "15인치 골드");
  }

  @Test
  void 상품_조회_연관된_옵션들_모두_조회() {
    // given
    List<OptionRequestDto> optionDtos = List.of(
        new OptionRequestDto("13인치 스타라이트", 40),
        new OptionRequestDto("15인치 미드나이트", 30),
        new OptionRequestDto("15인치 스페이스 그레이", 25)
    );

    ProductRequestDto request = new ProductRequestDto(
        "맥북 에어", 1500000, "macbook-air.url", optionDtos);
    ProductResponseDto savedResponse = productService.saveProduct(request); // DTO만 전달

    // when
    ProductResponseDto response = productService.findProductById(savedResponse.id());

    // then
    assertThat(response.name()).isEqualTo("맥북 에어");
    assertThat(response.options()).hasSize(3);

    List<OptionResponseDto> optionResponses = response.options();
    assertThat(optionResponses)
        .extracting(OptionResponseDto::name)
        .containsExactlyInAnyOrder("13인치 스타라이트", "15인치 미드나이트", "15인치 스페이스 그레이");
    assertThat(optionResponses)
        .extracting(OptionResponseDto::quantity)
        .containsExactlyInAnyOrder(40, 30, 25);
  }

  @Test
  void 존재하지_않는_상품_수정시_예외_발생() {
    // given
    Long nonExistentProductId = 999L;
    List<OptionRequestDto> options = List.of(
        new OptionRequestDto("13인치 실버", 20)
    );
    ProductRequestDto request = new ProductRequestDto(
        "존재하지 않는 상품", 1000000, "nonExistent.url", options);

    // when & then
    assertThatThrownBy(() -> productService.updateProduct(nonExistentProductId, request))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void Cascade_OrphanRemoval_테스트() {
    // given
    List<OptionRequestDto> initialOptionDtos = List.of(
        new OptionRequestDto("13인치 초기 옵션1", 30),
        new OptionRequestDto("13인치 초기 옵션2", 25)
    );

    ProductRequestDto initialRequest = new ProductRequestDto(
        "테스트 노트북", 1800000, "test-notebook.url", initialOptionDtos);
    ProductResponseDto savedResponse = productService.saveProduct(initialRequest);
    Long productId = savedResponse.id();

    Product initialProduct = productRepository.findById(productId).orElseThrow();
    assertThat(initialProduct.getOptions()).hasSize(2);

    // when
    List<OptionRequestDto> newOptionDtos = List.of(
        new OptionRequestDto("15인치 새로운 옵션", 50)
    );

    ProductRequestDto updateRequest = new ProductRequestDto(
        "수정된 노트북", 2200000, "updated-notebook.jpg", newOptionDtos);
    productService.updateProduct(productId, updateRequest);

    // then
    Product updatedProduct = productRepository.findById(productId).orElseThrow();
    assertThat(updatedProduct.getOptions()).hasSize(1);
    assertThat(updatedProduct.getOptions().get(0).getName()).isEqualTo("15인치 새로운 옵션");
  }
}