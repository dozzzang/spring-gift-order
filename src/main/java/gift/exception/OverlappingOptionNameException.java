package gift.exception;

public class OverlappingOptionNameException extends BusinessException {

  public OverlappingOptionNameException() {
    super(ErrorCode.OPTION_OVERLAPPING);
  }
}
