package gift.order.entity;

import gift.exception.ErrorCode;
import gift.exception.InsufficientStockException;
import gift.option.entity.Option;
import gift.product.entity.Product;
import gift.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "option_id", nullable = false)
  private Option option;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private LocalDateTime orderDateTime;

  @Column(length = 100)
  private String message;

  protected Order() {}

  public Order(final User user, final Option option, final int quantity, final String message) {
    this.user = user;
    this.option = option;
    this.quantity = quantity;
    this.message = message;
    this.orderDateTime = LocalDateTime.now();
  }

  public Long getId() { return id; }
  public User getUser() { return user; }
  public Option getOption() { return option; }
  public int getQuantity() { return quantity; }
  public LocalDateTime getOrderDateTime() { return orderDateTime; }
  public String getMessage() { return message; }

}