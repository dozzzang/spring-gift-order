package gift.exception;

public class KakaoApiErrorException extends RuntimeException {

  private ErrorCode errorCode;

  public KakaoApiErrorException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
