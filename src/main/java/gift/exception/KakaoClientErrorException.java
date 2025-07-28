package gift.exception;

public class KakaoClientErrorException extends BusinessException {

  public KakaoClientErrorException() {
    super(ErrorCode.KAKAO_CLIENT_ERROR);
  }

}
