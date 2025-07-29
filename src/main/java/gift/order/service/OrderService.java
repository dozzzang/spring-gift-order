package gift.order.service;

import gift.exception.ErrorCode;
import gift.exception.InsufficientStockException;
import gift.exception.OptionNotFoundException;
import gift.exception.UserNotFoundException;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import gift.wish.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OptionRepository optionRepository;
  private final WishRepository wishRepository;

  public OrderService(final OrderRepository orderRepository,
      final UserRepository userRepository,
      final OptionRepository optionRepository,
      final WishRepository wishRepository) {
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
    this.optionRepository = optionRepository;
    this.wishRepository = wishRepository;
  }

  @Transactional
  public OrderResponseDto createOrder(Long userId, OrderRequestDto request) {
     User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());

     Option option = optionRepository.findById(request.optionId())
        .orElseThrow(() -> new OptionNotFoundException());

    option.subtract(request.quantity());

    wishRepository.deleteByUserIdAndProductId(user.getId(),option.getProduct().getId());

    Order order = new Order(user, option, request.quantity(), request.message());
    Order savedOrder = orderRepository.save(order);

    // sendKakaoMessage(user, order);

    return OrderResponseDto.from(savedOrder);
  }
}
