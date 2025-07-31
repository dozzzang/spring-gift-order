package gift.order.controller;

import gift.auth.util.AuthUtil;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;
  private final AuthUtil authUtil;

  public OrderController(OrderService orderService,
      AuthUtil authUtil) {
    this.orderService = orderService;
    this.authUtil = authUtil;
  }

  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @Valid @RequestBody OrderRequestDto request,
      HttpServletRequest httpRequest) {

    Long userId = authUtil.getUserIdFromRequest(httpRequest);
    OrderResponseDto response = orderService.createOrder(userId, request);

    try {
      orderService.sendKakaoMessage(userId, response.id());
    } catch (Exception e) {
      System.err.println("카카오 메시지 전송 실패: " + e.getMessage());
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
