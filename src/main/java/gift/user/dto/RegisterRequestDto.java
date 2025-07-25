package gift.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequestDto(
    @NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
    String email,

    @NotBlank(message = "비밀번호는 반드시 입력되어야 합니다.")
    @Pattern(regexp = ".*[A-Z].*", message = "비밀번호에는 반드시 1글자 이상의 대문자가 포함되어야 합니다.")
    @Pattern(regexp = ".*\\d.*\\d.*\\d.*", message = "비밀번호에는 반드시 숫자가 3개 이상 포함되어야 합니다.")
    @Pattern(regexp = ".*[!@#$,].*", message = "비밀번호에는 반드시 !,@,#,$,. 중 하나 이상의 특수문자가 포함되어야 합니다.")
    String password
) {

}
