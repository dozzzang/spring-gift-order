package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import gift.product.dto.ProductRequestDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductPageTest {

  @Autowired
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.save(new Product("product1",10000,"thisisurl1"));
    productRepository.save(new Product("product2",10000,"thisisurl2"));
    productRepository.save(new Product("product3",10000,"thisisurl3"));
    productRepository.save(new Product("product4",10000,"thisisurl4"));
    productRepository.save(new Product("product5",10000,"thisisurl5"));
    productRepository.save(new Product("product6",10000,"thisisurl6"));
    productRepository.save(new Product("product7",10000,"thisisurl7"));
    productRepository.save(new Product("product8",10000,"thisisurl8"));
  }

  @Test
  void 페이지_통합_테스트() throws Exception {
    PageRequest pageRequest = PageRequest.of(0,7, Sort.by(Direction.DESC, "name"));

    Page<Product> page = productRepository.findAll(pageRequest);

    Page<ProductRequestDto> toMap = page.map(product ->
        new ProductRequestDto(product.getName(), product.getPrice(), product.getImageUrl()));

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
