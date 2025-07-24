package gift.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

  private static final String algorithm = "PBKDF2WithHmacSHA1";
  private static final String hashAlgorithm = "SHA-512";
  private static final String encoding = "UTF-8";
  private static final int iterationCount = 77777;
  private static final int keyLength = 128;

  public String encrypt(String email, String password) {
    try {
      byte[] salt = generateSalt(email);
      byte[] hash = createHash(password, salt);
      return encodeToBase64(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private byte[] generateSalt(String email)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
    byte[] emailBytes = email.getBytes(encoding);
    return digest.digest(emailBytes);
  }

  private byte[] createHash(String password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
    SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
    return factory.generateSecret(spec).getEncoded();
  }

  private String encodeToBase64(byte[] hash) {
    return Base64.getEncoder().encodeToString(hash);
  }

  public boolean isMatched(String email, String originalPassword, String encodedPassword) {
    try {
      String encrypted = encrypt(email, originalPassword);
      return encrypted.equals(encodedPassword);
    } catch (Exception e) {
      return false;
    }
  }

}
