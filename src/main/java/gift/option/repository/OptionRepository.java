package gift.option.repository;
import gift.option.entity.Option;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
  List<Option> findByProductId(Long productId);
  boolean existsByProductIdAndName(Long productId, String name);
}
