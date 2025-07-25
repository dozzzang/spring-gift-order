package gift.exception;

public class WishNotFoundException extends BusinessException{
  public WishNotFoundException() {
    super(ErrorCode.WISH_NOT_FOUND);
  }

}
