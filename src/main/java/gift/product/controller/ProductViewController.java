package gift.product.controller;

import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductViewController {

  private final ProductService productService;

  public ProductViewController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/")
  public String index(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "7")int size,
      @RequestParam(defaultValue = "name") String sort,
      Model model,
      HttpServletRequest request) {

    String token = getCookieValue(request, "token");
    String email = getCookieValue(request, "email");

    model.addAttribute("token", token);
    model.addAttribute("email", email);

    PageRequestDto pageRequestDto = new PageRequestDto(page, size, sort);
    Page<ProductResponseDto> products = productService.findAllProducts(pageRequestDto);
    model.addAttribute("products", products);
    return "product/index";
  }

  @GetMapping("/products")
  public String productList(Model model) {
    return "redirect:/";
  }

  @GetMapping("/products/new")
  public String moveForm() {
    return "product/form";
  }

  @PostMapping("/products")
  public String createProduct(@RequestParam String name, @RequestParam int price,
      @RequestParam String imageUrl) {
    ProductRequestDto dto = new ProductRequestDto(name, price, imageUrl);
    productService.saveProduct(dto);
    return "redirect:/";
  }

  @GetMapping("/products/{id}")
  public String findProduct(@PathVariable Long id, Model model) {
    ProductResponseDto dto = productService.findProductById(id);
    model.addAttribute("product", dto);
    return "product/detail";
  }

  @GetMapping("/products/{id}/update")
  public String moveUpdateForm(@PathVariable Long id, Model model) {
    ProductResponseDto product = productService.findProductById(id);
    model.addAttribute("product", product);
    return "product/update";
  }

  @PostMapping("/products/{id}")
  public String updateProduct(@PathVariable Long id, @RequestParam String name,
      @RequestParam int price, @RequestParam String imageUrl) {
    ProductRequestDto dto = new ProductRequestDto(name, price, imageUrl);
    productService.updateProduct(id, dto);
    return "redirect:/products/" + id;
  }

  @GetMapping("/products/{id}/delete")
  public String deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return "redirect:/";
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
