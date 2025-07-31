package gift.exception;

public class KakaoMessageTemplateException extends BusinessException {

  public KakaoMessageTemplateException(ErrorCode errorCode) {
    super(errorCode);
  }
}
