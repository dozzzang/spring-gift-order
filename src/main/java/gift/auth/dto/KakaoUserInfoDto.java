package gift.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoDto (
  Long id,

  @JsonProperty("kakao_account")
  KakaoAccount kakaoAccount
) {

  public String getEmailSafely() {

     if(kakaoAccount != null && kakaoAccount.email() != null) {
       return kakaoAccount().email();
     }

     return "이메일 정보 없는 상태";
  }

  public record KakaoAccount(
      String email
  ) {
  }
}
