package gift.option.dto;

import gift.option.entity.Option;

public record OptionResponseDto(
    Long id,
    int quantity,
    String name
) {
    public static OptionResponseDto from(Option option) {
      return new OptionResponseDto(
          option.getId(),
          option.getQuantity(),
          option.getName()
      );
    }
}
