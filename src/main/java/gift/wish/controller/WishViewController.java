package gift.wish.controller;

import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.user.entity.User;
import gift.user.service.UserService;
import gift.wish.dto.WishRequestDto;
import gift.wish.service.WishService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wishlist")
public class WishViewController {

  private final WishService wishService;
  private final UserService userService;

  public WishViewController(WishService wishService, UserService userService) {
    this.wishService = wishService;
    this.userService = userService;
  }

  @GetMapping
  public String index(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "7") int size,
      @RequestParam(defaultValue = "id") String sort,
      HttpServletRequest request,
      Model model) {

    String token = getCookieValue(request, "token");

    if (token == null) {
      return "redirect:/users/login";
    }

    User user = userService.findUserByToken(token);

    String userEmail = getCookieValue(request, "userEmail");
    model.addAttribute("token", token);
    model.addAttribute("userEmail", userEmail);

    PageRequestDto pageRequestDto = new PageRequestDto(page, size, sort);
    Page<ProductResponseDto> wishes = wishService.getWishes(user.getId(), pageRequestDto);

    model.addAttribute("wishes", wishes);
    return "product/wish";
  }

  @GetMapping("/add")
  public String showAddForm(HttpServletRequest request) {
    String token = getCookieValue(request, "token");

    if (token == null) {
      return "redirect:/users/login";
    }

    return "product/wish_add";
  }

  @PostMapping("/add")
  public String addToWishlist(@RequestParam Long productId, HttpServletRequest request) {
    String token = getCookieValue(request, "token");

    if (token == null) {
      return "redirect:/users/login";
    }

    User user = userService.findUserByToken(token);
    WishRequestDto dto = new WishRequestDto(productId);

      wishService.createWish(user.getId(), dto);

    return "redirect:/wishlist";
  }
  @GetMapping("/{productId}/delete")
  public String deleteWish(@PathVariable Long productId, HttpServletRequest request) {
    String token = getCookieValue(request, "token");

    if (token == null) {
      return "redirect:/users/login";
    }

    User user = userService.findUserByToken(token);
    wishService.deleteWish(user.getId(), productId);

    return "redirect:/wishlist";
  }

  private String getCookieValue(HttpServletRequest request, String cookieName) {
    return request.getCookies() != null ?
        Arrays.stream(request.getCookies())
            .filter(cookie -> cookieName.equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null) : null;
  }
}
