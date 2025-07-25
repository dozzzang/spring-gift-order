package gift.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoDto (
  Long id,

  @JsonProperty("kakao_account")
  KakaoAccount kakaoAccount
) {

  //앱에서 이메일 정보 허용이 불가한 상태
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
