package gift.auth.repository;

import gift.auth.entity.UserKakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserKakaoTokenRepository extends JpaRepository<UserKakaoToken, Long> {

}
