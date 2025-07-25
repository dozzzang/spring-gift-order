package gift.exception;

public class InvalidLoginException extends BusinessException {

  public InvalidLoginException() {
    super(ErrorCode.INVALID_LOGIN);
  }
}
