package gift.exception;

public class KakaoApprovalException extends BusinessException{
  public KakaoApprovalException() {
    super(ErrorCode.KAKAO_APPROVAL_REQUIRED);
  }
}
