package gift.exception;

public class ReauthorizeRequiredException extends RuntimeException {

  private ErrorCode errorCode;

  public  ReauthorizeRequiredException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

}
