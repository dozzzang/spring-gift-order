package gift.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.product.entity.Product;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  void save() {
    User expected = new User("user@user.com", "password");
    User actual = userRepository.save(expected);

    assertThat(actual.getId()).isNotNull();
    assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    assertThat(actual.getEncodedPassword()).isEqualTo(expected.getEncodedPassword());
  }

  @Test
  @Transactional
  void updateUser() {
    User savedUser = userRepository.save(new User("user@user.com", "password"));
    Long savedId = savedUser.getId();

    savedUser.setEmail("updatedUser@user.com");
    savedUser.setEncodedPassword("updatedPassword");

    entityManager.flush();
    entityManager.clear();

    User result = userRepository.findById(savedId).orElseThrow();
    assertThat(result.getEmail()).isEqualTo("updatedUser@user.com");
    assertThat(result.getEncodedPassword()).isEqualTo("updatedPassword");
  }

  @Test
  void findByEmail() {
    User savedUser = userRepository.save(new User("user@user.com", "password"));
    assertThat(savedUser.getId()).isNotNull();

    Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());
    assertThat(foundUser.isPresent());
    assertThat(foundUser.get().getEmail()).isEqualTo(savedUser.getEmail());
  }

  @Test
  void findByEmail_NotFound() {
    Optional<User> foundUser = userRepository.findByEmail("user@user.com");
    assertThat(foundUser.isPresent()).isFalse();
  }

  @Test
  void findById() {
    User savedUser = userRepository.save(new User("iser@user.com", "password"));
    Long savedId = savedUser.getId();

    Optional<User> foundUser = userRepository.findById(savedId);
    assertThat(foundUser.isPresent()).isTrue();
    assertThat(foundUser.get().getEmail()).isEqualTo(savedUser.getEmail());
  }

  @Test
  void deleteById() {
    User savedUser = userRepository.save(new User("user@user.com", "password"));
    Long savedId = savedUser.getId();

    userRepository.deleteById(savedId);

    Optional<User> foundUser = userRepository.findById(savedId);
    assertThat(foundUser.isPresent()).isFalse();
  }

  @Test
  void findAll() {
    userRepository.save(new User("user1@user.com", "password1"));
    userRepository.save(new User("user2@user.com", "password2"));

    Iterable<User> allUsers = userRepository.findAll();

    int count = 0;
    for (User user : allUsers) {
      count++;
    }

    assertThat(count).isEqualTo(2);
  }



}
