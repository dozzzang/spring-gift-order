package gift.order.repository;

import gift.order.entity.Order;
import gift.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUser(User user);

}
