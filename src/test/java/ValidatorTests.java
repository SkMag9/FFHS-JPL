import core.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidatorTests {


  @ParameterizedTest
  @ValueSource(strings = {"127.0.0.1", "192.168.0.100", "10.10.0.1"})
  void TestValidIp(String ip) {
    boolean result = Validator.isValidIP(ip);
    Assertions.assertTrue(result);
  }

  @Test
  void TestEmptyIpIsValid() {
    String ip = "";
    boolean result = Validator.isValidIP(ip);
    Assertions.assertTrue(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"127.0.0.1.1", "test", "10.0.0"})
  void TestInvalidIp(String ip) {
    boolean result = Validator.isValidIP(ip);
    Assertions.assertFalse(result);
  }
}
