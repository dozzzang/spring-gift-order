package gift.user.controller;

import gift.security.AdminInterceptor;
import gift.user.dto.UserRequestDto;
import gift.user.dto.UserResponseDto;
import gift.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/admin")
public class AdminViewController {

  private final UserService userService;
  private final AdminInterceptor adminInterceptor;

  public AdminViewController(UserService userService, AdminInterceptor adminInterceptor) {
    this.userService = userService;
    this.adminInterceptor = adminInterceptor;
  }

  @GetMapping
  public String index(Model model, HttpServletRequest req, HttpServletResponse res)
      throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    List<UserResponseDto> users = userService.findAllUsers();
    model.addAttribute("users", users);
    return "admin/index";
  }

  @GetMapping("/users/new")
  public String moveForm(HttpServletRequest req, HttpServletResponse res) throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    return "admin/form";
  }

  @PostMapping
  public String createUser(UserRequestDto userRequestDto
      , HttpServletRequest req, HttpServletResponse res)
      throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    userService.saveUser(userRequestDto);
    return "redirect:/api/admin";
  }

  @GetMapping("/users/{id}")
  public String findUser(@PathVariable Long id, Model model, HttpServletRequest req,
      HttpServletResponse res)
      throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    UserResponseDto dto = userService.findById(id);
    model.addAttribute("user", dto);
    return "admin/detail";
  }

  @GetMapping("/users/{id}/update")
  public String moveUpdateForm(@PathVariable Long id, Model model, HttpServletRequest req,
      HttpServletResponse res) throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    UserResponseDto user = userService.findById(id);
    model.addAttribute("user", user);
    return "admin/update";
  }

  @PostMapping("/users/{id}")
  public String updateUser(@PathVariable Long id, @RequestParam String email,
      @RequestParam String password, HttpServletRequest req, HttpServletResponse res) throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    UserRequestDto dto = new UserRequestDto(email, password);
    userService.updateUser(id, dto);
    return "redirect:/api/admin/users/" + id;
  }

  @GetMapping("/users/{id}/delete")
  public String deleteUser(@PathVariable Long id, HttpServletRequest req, HttpServletResponse res)
  throws Exception {

    if (!adminInterceptor.isAdmin(req, res)) {
      return null;
    }

    userService.deleteUser(id);
    return "redirect:/api/admin";
  }
}