package gift.product.entity;

import gift.exception.OverlappingOptionNameException;
import gift.option.entity.Option;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 15)
  private String name;
  @Column(nullable = false)
  private int price;
  @Column(nullable = false)
  private String imageUrl;
  @Column(nullable = false)
  private boolean kakaoApproval = false;
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Option> options = new ArrayList<>();

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Product(Long id, String name, int price, String imageUrl) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.imageUrl = imageUrl;
  }

  public List<Option> getOptions() {
    return options;
  }

  public Product(String name, int price, String imageUrl,boolean kakaoApproval) {
    this.name = name;
    this.price = price;
    this.imageUrl = imageUrl;
    this.kakaoApproval = kakaoApproval;
  }

  public Product(String name, int price, String imageUrl) {
    this.name = name;
    this.price = price;
    this.imageUrl = imageUrl;
  }

  //kakaoApproval validation을 위해 Getter가 3개나 추가..
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getPrice() {
    return price;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public boolean isKakaoApproval() {
    return kakaoApproval;
  }

  protected Product() {
  }

  private void validateUniqueOptionNames(String optionName) {
    for(Option option : this.options) {
      if(option.getName().equals(optionName)) {
        throw new OverlappingOptionNameException();
      }
    }
  }

  public void addOption(Option option) {
    validateUniqueOptionNames(option.getName());
    option.setProduct(this);
    options.add(option);
  }
}
