package gift.exception;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

  private String message;
  private int status;
  private String code;
  private List<FieldError> errors;

  // 에러코드 객체만 들어왔다면 그 객체를 이용해서 에러응답객체를
  private ErrorResponse(final ErrorCode errorCode) {
    this(errorCode,new ArrayList<>());
  }

  // 에러코드 객체와, 필드에러 리스트가 들어왔을때 응답객체 생성
  private ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors) {
    this.message = errorCode.getMessage();
    this.status = errorCode.getStatus().value();
    this.code = errorCode.getCode();
    this.errors = errors;
  }

  // 에러코드만 받았을때
  public static ErrorResponse of(final ErrorCode errorCode) {
    return new ErrorResponse(errorCode);
  }

  // 에러코드와 필드에러 리스트를 받았을때
  public static ErrorResponse of(final ErrorCode errorCode, final List<FieldError> errors) {
    return new ErrorResponse(errorCode, errors);
  }

  public String getMessage() {
    return message;
  }

  public int getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }

  public List<FieldError> getErrors() {
    return errors;
  }
}
