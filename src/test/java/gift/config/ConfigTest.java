package gift.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ConfigTest {

  @Autowired
  private MockMvc mockMvc;

  private static final String ALLOWED_METHODS = "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS";

  @Test
  void cors_정상동작() throws Exception {
      mockMvc.perform(options("/api/products")
              .header(HttpHeaders.ORIGIN, "http://localhost:8080")
              .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
          .andExpect(status().isOk())
          .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
          .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
              "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS"))
          .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
              HttpHeaders.LOCATION))
          .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1200"));
    }

  @Test
  void corsForDifferentMethods() throws Exception {
    // POST
    mockMvc.perform(options("/api/products")
            .header(HttpHeaders.ORIGIN, "http://localhost:3000")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));

    // DELETE
    mockMvc.perform(options("/api/products")
            .header(HttpHeaders.ORIGIN, "http://localhost:3000")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE"))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
  }
}
