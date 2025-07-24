package gift.wish.controller;

import gift.product.dto.ProductResponseDto;
import gift.security.LoginUser;
import gift.user.entity.User;
import gift.wish.dto.WishRequestDto;
import gift.wish.dto.WishResponseDto;
import gift.wish.service.WishService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WishRestController {

  private final WishService wishService;

  public WishRestController(WishService wishService) {
    this.wishService = wishService;
  }

  @PostMapping("/api/wishes")
  public ResponseEntity<WishResponseDto> createWish(
      @Valid @RequestBody WishRequestDto wishRequestDto,
      @LoginUser User user
  ) {
    WishResponseDto wishResponseDto = wishService.createWish(user.getId(), wishRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(wishResponseDto);
  }

  @GetMapping("/api/wishes")
  public ResponseEntity<List<ProductResponseDto>> getWishes(
      @LoginUser User user
  ) {
    List<ProductResponseDto> products = wishService.getWishes(user.getId());
    return ResponseEntity.ok(products);
  }

  @DeleteMapping("/api/wishes/{productId}")
  public ResponseEntity<Void> deleteWish(@PathVariable Long productId,
      @LoginUser User user) {
    wishService.deleteWish(user.getId(), productId);
    return ResponseEntity.noContent().build();
  }

}
