package core;

/**
 * Class with static Command Values, to determine what action is to be executed on server or client
 */
public class Commands {

  public static final String SET_NAME = "SetName";
  public static final String ANSWER = "Answer";
  public static final String GET_ANSWER = "GetAnswer";
  public static final String END_GAME = "EndGame";

  /**
   * hide the implicit public constructor
   */
  private Commands() {
  }
}
