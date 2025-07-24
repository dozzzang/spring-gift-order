package gift.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.option.entity.Option;
import org.junit.jupiter.api.Test;

class OptionTest {

  @Test
  void 옵션_생성_성공() {
    // given
    String name = "13인치 스페이스 그레이";
    int quantity = 50;

    // when
    Option option = new Option(name, quantity);

    // then
    assertThat(option.getName()).isEqualTo("13인치 스페이스 그레이");
    assertThat(option.getQuantity()).isEqualTo(50);
  }

  @Test
  void 재고_정상_차감() {
    // given
    Option option = new Option("15인치 실버", 100);

    // when
    option.subtract(30);

    // then
    assertThat(option.getQuantity()).isEqualTo(70);
  }

  @Test
  void 영_미만_수량차감_예외_발생() {
    // given
    Option option = new Option("15인치 골드", 100);

    // when & then
    assertThatThrownBy(() -> option.subtract(0))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> option.subtract(-5))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 연속적인_재고_차감_정상동작() {
    // given
    Option option = new Option("13인치 스타라이트", 100);

    // when
    option.subtract(20);
    option.subtract(30);
    option.subtract(10);

    // then
    assertThat(option.getQuantity()).isEqualTo(40);
  }
}