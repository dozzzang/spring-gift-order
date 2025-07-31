package gift.exception;

public class OrderNotFoundException extends BusinessException {

  public OrderNotFoundException() {
    super(ErrorCode.ORDER_NOT_FOUND);
  }
}

