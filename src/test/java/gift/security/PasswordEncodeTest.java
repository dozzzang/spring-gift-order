package gift.security;

import static org.assertj.core.api.Assertions.assertThat;

import gift.user.entity.Role;
import gift.user.entity.User;
import org.junit.jupiter.api.Test;

public class PasswordEncodeTest {
    @Test
    void User객체생성시_비밀번호_인코드_테스트() {
      //given
      PasswordEncoder passwordEncoder = new PasswordEncoder();
      String email = "admin@example.com";
      String originalPassword = "password";

      //when
      User user = new User(1L,email,passwordEncoder.encrypt(email,originalPassword), Role.USER);

      //then
      assertThat(passwordEncoder.encrypt(email,originalPassword)).isEqualTo(user.getEncodedPassword());
    }


}
