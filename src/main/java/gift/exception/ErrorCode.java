package gift.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  //Bean Validation!
  VALIDATION_FAILED("VALIDATION_FAILED", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

  //요구사항 field validation!
  INVALID_PRODUCT_NAME("INVALID_PRODUCT_NAME", "상품명이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PRODUCT_PRICE("INVALID_PRODUCT_PRICE", "상품 가격이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_IMAGE_URL("INVALID_IMAGE_URL", "이미지 URL이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  KAKAO_APPROVAL_REQUIRED("KAKAO_APPROVAL_REQUIRED","카카오 관련 상품명은 MD승인이 필요합니다.",HttpStatus.BAD_REQUEST),
  OPTION_OVERLAPPING("OPTION_OVERAPPING","옵션의 이름이 중복되고 있습니다. 이름은 유일해야 합니다.",HttpStatus.BAD_REQUEST),

  INVALID_PASSWORD("INVALID_PASSWORD","회원가입 비밀번호 형식이 다릅니다.",HttpStatus.BAD_REQUEST),
  INVALID_LOGIN("INVALID_LOGIN", "이메일 또는 비밀번호가 일치하지 않습니다.",HttpStatus.FORBIDDEN),
  USER_NOT_FOUND("USER_NOT_FOUND","존재하지 않는 사용자입니다.",HttpStatus.NOT_FOUND),
  PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND","존재하지 않는 상품입니다.",HttpStatus.NOT_FOUND),
  WISH_NOT_FOUND("WISH_NOT_FOUND","위시리스트가 존재하지 않습니다.",HttpStatus.NOT_FOUND),
  OPTION_NOT_FOUND("OPTION_NOT_FOUND","옵션이 존재하지 않습니다.",HttpStatus.NOT_FOUND),

  WISH_ALREADY_EXISTED("WISH_ALREADY_EXISTED","위시리스트가 이미 존재합니다.",HttpStatus.BAD_REQUEST),

  INVALID_JWT("INVALID_JWT","유효하지 않은 토큰입니다.",HttpStatus.UNAUTHORIZED),

  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR","내부 서버에서 오류가 발생하였습니다.",HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;

  ErrorCode(String code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
