package gift.user.service;

import gift.exception.InvalidLoginException;
import gift.exception.UserNotFoundException;
import gift.security.PasswordEncoder;
import gift.user.JwtTokenProvider;
import gift.user.dto.LoginRequestDto;
import gift.user.dto.LoginResponseDto;
import gift.user.dto.RegisterRequestDto;
import gift.user.dto.UserRequestDto;
import gift.user.dto.UserResponseDto;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
  }

  private User findByIdOrFail(Long id) {
    User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    return user;
  }

  @Transactional
  public String handleKaKaoLogin(String email, String kakaoId) {
    User user = userRepository.findByEmail(email).orElseGet(
        () -> {
          User newUser = new User(email,"reservedPassword" + kakaoId);
          return userRepository.save(newUser);
        }
        );
    return jwtTokenProvider.generateToken(user);
  }

  @Transactional
  public void registerUser(RegisterRequestDto registerRequestDto) {
    String encryptedPassword = passwordEncoder.encrypt(registerRequestDto.email(),
        registerRequestDto.password());
    User user = new User(registerRequestDto.email(), encryptedPassword);
    userRepository.save(user);
  }

  @Transactional
  public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
    User user = userRepository.findByEmail(loginRequestDto.email())
        .orElseThrow(UserNotFoundException::new);

    if (!user.isEqualPassword(loginRequestDto.password(), passwordEncoder)) {
      throw new InvalidLoginException();
    }

    String token = jwtTokenProvider.generateToken(user);

    return new LoginResponseDto(token);
  }

  @Transactional(readOnly = true)
  public List<UserResponseDto> findAllUsers() {
    return userRepository.findAll().stream()
        .map(UserResponseDto::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public UserResponseDto saveUser(UserRequestDto dto) {
    String encryptedPassword = passwordEncoder.encrypt(dto.email(), dto.password());
    User user = new User(dto.email(), encryptedPassword);
    User savedUser = userRepository.save(user);
    return UserResponseDto.from(savedUser);
  }

  @Transactional(readOnly = true)
  public UserResponseDto findById(Long userId) {
    User user = findByIdOrFail(userId);
    return UserResponseDto.from(user);
  }

  @Transactional
  public UserResponseDto updateUser(Long userId, UserRequestDto dto) {
    User user = findByIdOrFail(userId);
    String finalPassword;

    if (dto.password() == null || dto.password().trim().isEmpty()) {
      finalPassword = user.getEncodedPassword();
    } else {
      finalPassword = passwordEncoder.encrypt(dto.email(), dto.password());
    }

    user.setEmail(dto.email());
    user.setEncodedPassword(finalPassword);

    return UserResponseDto.from(user);
  }

  @Transactional
  public void deleteUser(Long userId) {
    findByIdOrFail(userId);
    userRepository.deleteById(userId);
  }

  @Transactional
  public User findUserByToken(String token) {
    String email = jwtTokenProvider.getEmail(token);
    User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    return user;
  }


}