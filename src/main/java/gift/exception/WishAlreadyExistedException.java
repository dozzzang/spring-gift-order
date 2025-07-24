package gift.exception;

public class WishAlreadyExistedException extends BusinessException {

  public WishAlreadyExistedException() {
    super(ErrorCode.WISH_ALREADY_EXISTED);
  }
}
