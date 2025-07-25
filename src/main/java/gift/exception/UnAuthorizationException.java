package gift.exception;

public class UnAuthorizationException extends RuntimeException {

  private final ErrorCode errorCode;

  public UnAuthorizationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

}
