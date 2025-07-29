package gift.exception;

public class InsufficientStockException extends BusinessException {
  private ErrorCode errorCode;

  public  InsufficientStockException(ErrorCode errorCode) {
    super(errorCode);
  }
}
