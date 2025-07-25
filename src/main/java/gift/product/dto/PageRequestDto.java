package gift.product.dto;

public record PageRequestDto (
  int page,
  int size,
  String sort
)

{
}
