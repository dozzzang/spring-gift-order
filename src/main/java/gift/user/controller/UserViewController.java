package gift.user.controller;

import gift.user.dto.LoginRequestDto;
import gift.user.dto.LoginResponseDto;
import gift.user.dto.RegisterRequestDto;
import gift.user.dto.UserRequestDto;
import gift.user.dto.UserResponseDto;
import gift.user.entity.User;
import gift.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserViewController {

  private final UserService userService;

  UserViewController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/register")
  public String moveRegisterForm() {
    return "product/register";
  }

  @GetMapping("/login")
  public String moveLoginForm() {
    return "product/login";
  }

  @GetMapping("/logout")
  public String logout(HttpServletResponse response) {
    Cookie emailCookie = new Cookie("email",null);
    Cookie tokenCookie = new Cookie("token",null);

    emailCookie.setMaxAge(0);
    tokenCookie.setMaxAge(0);
    emailCookie.setPath("/");
    tokenCookie.setPath("/");
    response.addCookie(emailCookie);
    response.addCookie(tokenCookie);

    return "redirect:/";
  }

  @PostMapping("/register")
  public String Register(@RequestParam String email,
      @RequestParam String password) {
      RegisterRequestDto dto = new RegisterRequestDto(email, password);
      userService.registerUser(dto);
      return "redirect:/";
  }

  @PostMapping("/login")
  public String Login(@RequestParam String email,
      @RequestParam String password,
      HttpServletResponse response) {
    LoginRequestDto dto = new LoginRequestDto(email, password);
    LoginResponseDto loginResponse = userService.loginUser(dto);

    Cookie emailCookie = new Cookie("email",email);
    Cookie tokenCookie = new Cookie("token",loginResponse.token());

    emailCookie.setMaxAge(1800);
    tokenCookie.setMaxAge(1800);
    emailCookie.setPath("/");
    tokenCookie.setPath("/");
    response.addCookie(emailCookie);
    response.addCookie(tokenCookie);

    return "redirect:/";
  }

  @GetMapping("/mypage")
  public String mypage(HttpServletRequest request, Model model) {
    String token = getCookieValue(request,"token");

    if(token == null) {
      return "redirect:/users/login";
    }

    User user = userService.findUserByToken(token);
    model.addAttribute("user", UserResponseDto.from(user));

    return "product/mypage";
  }

  @PostMapping("/mypage")
  public String updateMypage(@RequestParam String email,
      @RequestParam String password,
      HttpServletResponse response,
      HttpServletRequest request) {

      String token = getCookieValue(request,"token");

      if(token == null) {
        return "redirect:/users/login";
      }

    User user = userService.findUserByToken(token);
    UserRequestDto userRequestDto = new UserRequestDto(email,password);
    userService.updateUser(user.getId(),userRequestDto);

    Cookie emailCookie = new Cookie("email",email);
    emailCookie.setMaxAge(1800);
    emailCookie.setPath("/");
    response.addCookie(emailCookie);

    return "redirect:/users/mypage";

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
