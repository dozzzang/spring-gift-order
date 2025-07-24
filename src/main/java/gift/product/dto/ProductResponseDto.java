package gift.product.dto;

import gift.option.dto.OptionResponseDto;
import gift.product.entity.Product;
import java.util.List;

public record ProductResponseDto(
    Long id,
    String name,
    int price,
    String imageUrl,
    boolean kakaoApproval,
    List<OptionResponseDto> options
) {

  public static ProductResponseDto from(Product product) {
    List<OptionResponseDto> optionDtos = product.getOptions().stream()
        .map(OptionResponseDto::from)
        .toList();

    return new ProductResponseDto(
        product.getId(), product.getName(), product.getPrice(),
        product.getImageUrl(), product.isKakaoApproval(), optionDtos
    );
  }
}
