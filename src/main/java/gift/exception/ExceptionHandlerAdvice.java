package gift.exception;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {

    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(error -> new FieldError(
            error.getField(),
            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
            error.getDefaultMessage()
        ))
        .collect(Collectors.toList());

    ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.VALIDATION_FAILED, fieldErrors);

    return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {

    ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode());

    return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
  }

  @ExceptionHandler(UnAuthorizationException.class)
  public ResponseEntity<ErrorResponse> handleUnAuthorizationException(UnAuthorizationException ex) {

    ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode());

    return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
  }

  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException ex) {

    ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode());

    return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
  }

}