package gift.option.entity;

import gift.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "options")
public class Option {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  public Long getId() {
    return id;
  }

    public String getName() {
    return name;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public Product getProduct() {
    return product;
  }

  public Option(String name, int quantity, Product product) {
    this.name = name;
    this.quantity = quantity;
    this.product = product;
  }

  public Option(String name, int quantity) {
    this.name = name;
    this.quantity = quantity;
  }

    public void subtract(int quantity) {
      if (quantity <= 0) {
        throw new IllegalArgumentException("1 이상의 수량만 차감할 수 있습니다.");
      }
      if (this.quantity < quantity) {
        throw new IllegalStateException("최종 수량은 음수일 수 없습니다.");
      }
      this.quantity -= quantity;
    }

  protected Option() {

  }

}
