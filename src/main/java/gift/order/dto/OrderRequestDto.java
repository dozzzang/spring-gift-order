package gift.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
    @NotNull(message = "옵션 id는 필수로 기입되어야합니다.")
    Long optionId,

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    int quantity,

    @NotBlank(message = "메시지는 필수로 기입되어야합니다.")
    String message
) {
}