package gift.exception;

public class KakaoLoginErrorException extends RuntimeException {
  private ErrorCode errorCode;

  public KakaoLoginErrorException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
