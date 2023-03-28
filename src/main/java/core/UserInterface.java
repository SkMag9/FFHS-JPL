package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/**
 * Contains methods to interact with an user via the console
 */
public class UserInterface {

  private final Scanner scanner = new Scanner(System.in);

  /**
   * Shows an introduction to the game and asks the user, what action he wants to take.
   * @return q to quit, n to indicate that the user wants to create a new game, or j to join an existing game
   */
  public char showWelcomeScreen() {
    System.out.println("Welcome to the guessing game");
    System.out.println("The rules: each question has 3 answers. Type 'a' to select answer a.");
    System.out.println("The fastest player gets the point");
    System.out.println();
    String input;
    ArrayList<String> options = new ArrayList<>(
        Arrays.asList("n", "new", "j", "join", "q", "quit"));
    do {
      System.out.println(
          "To start a new game server, enter 'n(ew)', to join a existing game type 'j(oin)', to exit type 'q(uit)'");
      input = scanner.nextLine();
    } while (!options.contains(input));
    return input.charAt(0);
  }

  /**
   * Gets either a, b or c as the answer from the user. Input is handled case insensitive
   * @return char with the chosen answer
   */
  public char getAnswer() {
    String input;
    ArrayList<String> options = new ArrayList<>(
        Arrays.asList("A", "B", "C", "a", "b", "c"));
    do {
      System.out.println(
          "Which answer is correct? A, B or C?");
      input = scanner.nextLine();
    } while (!options.contains(input));
    return input.toUpperCase(Locale.ROOT).charAt(0);
  }

  /**
   * prints an error message to the output
   * @param message
   */
  public void showError(String message) {
    System.err.println(message);
  }

  /**
   * prompts the user to enter a name with a minimal length of 3
   * @return the name entered by the user
   */
  public String getName() {
    String name;
    do {
      System.out.println("Please enter your name (min 3 characters): ");
      name = scanner.nextLine();
    } while (name.length() < 3);
    return name;
  }

  /**
   * prompts for an integer between 2 and 4 for the number of players
   * @return number of players
   */
  public int getNumberOfPlayers() {
    int players;
    do {
      System.out.println("Please enter the number of players (2-4): ");
      players = scanner.nextInt();
    } while (players < 2 || players > 4);
    return players;
  }

  /**
   * Output the given message to the console
   * @param message
   */
  public void showMessage(String message) {
    System.out.println(message);
  }

  /**
   * prompts the user for the server ip he wants to join
   * @return the ip or an empty string for localhost
   */
  public String getServerIP() {
    String ip;
    do {
      System.out.println("Please enter the server ip: ");
      ip = scanner.nextLine();
    } while (!Validator.isValidIP(ip));
    return ip;
  }

  /**
   * prompts for an integer between 1 and the number of available questions
   * @param maxQuestions
   * @return number of questions chosen
   */
  public int getNumberOfQuestions(int maxQuestions) {
    int questions;
    do {
      System.out.printf("How many questions shall be asked? (max. %d)%n", maxQuestions);
      questions = scanner.nextInt();
    } while (questions < 1 || questions > maxQuestions);
    return questions;
  }
}
