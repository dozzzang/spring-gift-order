package gift.wish.service;

import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.exception.ProductNotFoundException;
import gift.exception.UserNotFoundException;
import gift.exception.WishNotFoundException;
import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import gift.wish.dto.WishRequestDto;
import gift.wish.dto.WishResponseDto;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final WishRepository wishRepository;

  public WishService(ProductRepository productRepository, UserRepository userRepository,
      WishRepository wishRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.wishRepository = wishRepository;
  }

  private User findUserByIdOrFail(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    return user;
  }

  private Product findProductByIdOrFail(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);
    return product;
  }


  public WishResponseDto createWish(Long userId, WishRequestDto wishRequestDto) {
    User foundUser = findUserByIdOrFail(userId);
    Product foundProduct = findProductByIdOrFail(wishRequestDto.productId());

    Optional<Wish> wish = wishRepository.findByUserIdAndProductId(userId,
        wishRequestDto.productId());
    if (wish.isPresent()) {
      throw new BusinessException(ErrorCode.WISH_ALREADY_EXISTED);
    }

    Wish newWish = new Wish(foundUser, foundProduct);
    Wish savedWish = wishRepository.save(newWish);

    return new WishResponseDto(
        savedWish.getId(),
        savedWish.getUser().getId(),
        savedWish.getProduct().getId()
    );
  }

  public List<ProductResponseDto> getWishes(Long userId) {
    return wishRepository.findByUserId(userId).stream()
        .map(wish -> wish.getProduct())
        .map(ProductResponseDto::from)
        .collect(Collectors.toList());
  }

  public Page<ProductResponseDto> getWishes(Long userId, PageRequestDto pageRequestDto) {
    Sort sortCondition = Sort.by(Direction.DESC,pageRequestDto.sort());
    Pageable pageable = PageRequest.of(pageRequestDto.page(), pageRequestDto.size(),sortCondition);

    Page<Wish> wishes = wishRepository.findByUserId(userId, pageable);
    return wishes.map(wish -> ProductResponseDto.from(wish.getProduct()));
  }

  @Transactional
  public void deleteWish(Long userId, Long productId) {
    wishRepository.findByUserIdAndProductId(userId, productId).orElseThrow(
        WishNotFoundException::new);
    wishRepository.deleteByUserIdAndProductId(userId, productId);
  }
}
