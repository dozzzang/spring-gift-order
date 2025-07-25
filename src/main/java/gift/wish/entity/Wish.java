package gift.wish.entity;

import gift.product.entity.Product;
import gift.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Wish {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private User user;

  @ManyToOne
  private Product product;

  protected Wish() {
  }

  public Wish(User user, Product product) {
    this.user = user;
    this.product = product;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public Product getProduct() {
    return product;
  }
}
