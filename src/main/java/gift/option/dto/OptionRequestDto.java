package gift.option.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OptionRequestDto(
  @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s()\\[\\]\\+\\-&/_]*$",
      message = "( ), [ ], +, -, &, /, _ 외 특수문자는 사용이 불가합니다.")
  @Size(max = 50, message = "옵션명은 50자 이하여야 합니다.")
  @NotBlank
  String name,
  @Min(value = 1, message = "수량은 최소 1개입니다.")
  @Max(value = 99_999_999, message = "수량은 1억 미만개여야 합니다.")
  int quantity
)
{

}
