package gift.exception;

public class FieldError {

  private String field;
  private String value;
  private String reason;

  public FieldError(String field, String value, String reason) {
    this.field = field;
    this.value = value;
    this.reason = reason;
  }

  public String getField() {
    return field;
  }

  public String getValue() {
    return value;
  }

  public String getReason() {
    return reason;
  }
}
