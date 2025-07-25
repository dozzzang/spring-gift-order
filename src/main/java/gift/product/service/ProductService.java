package gift.product.service;

import gift.exception.KakaoApprovalException;
import gift.exception.ProductNotFoundException;
import gift.option.dto.OptionResponseDto;
import gift.option.entity.Option;
import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  private Product findProductByIdOrFail(Long id) {
    return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public ProductResponseDto findProductById(Long productId) {
    Product product = findProductByIdOrFail(productId);
    return ProductResponseDto.from(product);
  }

  @Transactional(readOnly = true)
  public List<OptionResponseDto> getProductOptions(Long productId) {
    Product product = findProductByIdOrFail(productId);
    return product.getOptions().stream()
        .map(OptionResponseDto::from)
        .toList();
  }

  @Transactional
  public ProductResponseDto saveProduct(ProductRequestDto dto) {

    List<Option> options = dto.options().stream()
        .map(optionRequest -> new Option(optionRequest.name(), optionRequest.quantity()))
        .toList();

    Product product = new Product(dto.name(), dto.price(), dto.imageUrl());

    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }

    options.forEach(product::addOption);
    Product savedProduct = productRepository.save(product);

    return ProductResponseDto.from(savedProduct);
  }

  @Transactional
  public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {
    Product product = findProductByIdOrFail(productId);

    if (dto.name().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }

    product.setName(dto.name());
    product.setPrice(dto.price());
    product.setImageUrl(dto.imageUrl());
    product.getOptions().clear();

    List<Option> options = dto.options().stream().map(optionRequestDto -> new Option(
        optionRequestDto.name(), optionRequestDto.quantity())).toList();

    options.forEach(product::addOption);

    //Dirty checking
    return ProductResponseDto.from(product);
  }

  @Transactional
  public void deleteProduct(Long productId) {
    productRepository.deleteById(productId);
  }

  @Transactional(readOnly = true)
  public Page<ProductResponseDto> findAllProducts(PageRequestDto pageRequestDto) {
    Sort sortCondition = Sort.by(Direction.DESC, pageRequestDto.sort());
    Pageable pageable = PageRequest.of(pageRequestDto.page(), pageRequestDto.size(), sortCondition);

    Page<Product> products = productRepository.findAll(pageable);

    return products.map(ProductResponseDto::from);
  }
}