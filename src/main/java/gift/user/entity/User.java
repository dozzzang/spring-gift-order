package gift.user.entity;

import gift.security.PasswordEncoder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false,unique = true)
  private String email;
  @Column(nullable = false)
  private Role role = Role.USER;

  public void setEmail(String email) {
    this.email = email;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void setEncodedPassword(String encodedPassword) {
    this.encodedPassword = encodedPassword;
  }

  @Column(nullable = false)
  private String encodedPassword;

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getEncodedPassword() {
    return encodedPassword;
  }

  public Role getRole() {
    return role;
  }

  public User(Long id, String email, String encodedPassword, Role role) {
    this.id = id;
    this.email = email;
    this.encodedPassword = encodedPassword;
    this.role = role;
  }

  public User(Long id, String email, String encodedPassword) {
    this.email = email;
    this.encodedPassword = encodedPassword;
  }

  public User(String email, String encodedPassword) {
    this.email = email;
    this.encodedPassword = encodedPassword;
  }

  public boolean isEqualPassword(String password, PasswordEncoder passwordEncoder) {
    return passwordEncoder.isMatched(this.email, password, this.encodedPassword);
  }

  protected User() {

  }
}
