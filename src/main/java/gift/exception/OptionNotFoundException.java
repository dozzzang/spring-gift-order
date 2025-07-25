package gift.exception;

public class OptionNotFoundException extends BusinessException {

  public OptionNotFoundException() {
    super(ErrorCode.OPTION_NOT_FOUND);
  }
}
