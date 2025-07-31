package gift.auth.repository;

import gift.auth.entity.UserKakaoToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserKakaoTokenRepository extends JpaRepository<UserKakaoToken, Long> {
  Optional<UserKakaoToken> findByUserId(Long userId);
  void deleteByUserId(Long userId);
  boolean existsByUserId(Long userId);
}
