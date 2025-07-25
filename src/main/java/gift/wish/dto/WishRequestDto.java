package gift.wish.dto;

import jakarta.validation.constraints.NotNull;

public record WishRequestDto(
    @NotNull(message = "상품ID는 반드시 존재해야합니다.")
    Long productId
) {

}
