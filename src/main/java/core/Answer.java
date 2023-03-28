package core;

/**
 * Record holding an answer and the player which gave it
 */
public record Answer(char answer, Player player) {

  public char getAnswer() {
    return answer;
  }

  public Player getPlayer() {
    return player;
  }
}
