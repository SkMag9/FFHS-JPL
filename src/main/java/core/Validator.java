package core;

/**
 * Class to help with validation of user input
 */
public class Validator {

  /**
   * Private constructor to hide the implicit public one
   */
  private Validator() {
  }

  /**
   * Validate an IPv4 address
   * @param ip the ip to validate
   * @return true for valid ipv4 or empty string, false for invalid ip
   */
  public static boolean isValidIP(String ip) {
    //default value, will be interpreted as localhost
    if (ip.length() == 0) {
      return true;
    }
    //IP regex from https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
    String ipPattern = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
    return ip.matches(ipPattern);
  }
}
