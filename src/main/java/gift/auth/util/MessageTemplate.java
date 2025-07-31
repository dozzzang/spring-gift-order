package gift.auth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.exception.ErrorCode;
import gift.exception.InsufficientStockException;
import gift.exception.KakaoMessageTemplateException;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageTemplate {

  private final ObjectMapper objectMapper;

  public MessageTemplate(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String createOrderMessage(final String orderInfo, final String webUrl) {
    try {
      Map<String, Object> template = Map.of(
          "object_type", "text",
          "text", String.format("주문이 완료되었습니다! \n%s", orderInfo),
          "link", Map.of(
              "web_url", webUrl,
              "mobile_web_url", webUrl
          ),
          "button_title", "주문 확인"
      );
      return objectMapper.writeValueAsString(template);
    } catch (JsonProcessingException e) {
      throw new KakaoMessageTemplateException(ErrorCode.KAKAO_MESSAGE_ERROR);
    }
  }
}