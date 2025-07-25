package gift.exception;

public class InternalServerException extends RuntimeException {
  private ErrorCode errorCode;

  public InternalServerException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

}
