package gift.wish.repository;

import gift.wish.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish,Long> {


  List<Wish> findByUserId(Long userId);
  Page<Wish> findByUserId(Long userId, Pageable pageable);
  Optional<Wish> findByUserIdAndProductId(Long userId, Long productId);
  void deleteByUserIdAndProductId(Long userId, Long productId);
  boolean existsByUserIdAndProductId(Long userId, Long productId);
}
