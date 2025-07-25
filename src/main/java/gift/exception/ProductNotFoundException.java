package gift.exception;

public class ProductNotFoundException extends BusinessException{
  public ProductNotFoundException() {
    super(ErrorCode.PRODUCT_NOT_FOUND);
  }
}