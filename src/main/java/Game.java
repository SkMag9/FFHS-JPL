import core.Importer;
import core.Question;
import core.UserInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.Client;
import network.Server;

/**
 * main class, start or join a game
 */
public class Game implements Runnable {

  // use this as a default port
  private static final int PORT = 50000;
  private final UserInterface ui = new UserInterface();

  public static void main(String[] args) {
    new Game().run();
  }

  /**
   * show welcome screen and get desired action from user
   * starts or joins a game or quits
   */
  @Override
  public void run() {
    switch (ui.showWelcomeScreen()) {
      case 'n' -> createGame();
      case 'j' -> joinGame();
      default -> ui.showMessage("Goodbye");
    }
  }

  /**
   * start a new game as a server
   */
  private void createGame() {
    List<Question> questions;
    try {
      //load questions using the importer
      Importer questionsFile = new Importer(
          "src/main/resources/ftoop_multiplayerquiz_fragenkatalog_2021.txt");
      questions = questionsFile.getQuestions();
      ui.showMessage("Questions loaded");
    } catch (FileNotFoundException ex) {
      ui.showError("Questions file not found");
      return;
    }
    //get number of players and questions to play with
    int players = ui.getNumberOfPlayers();
    int numQuestions = ui.getNumberOfQuestions(questions.size());
    try {
      var server = new Server(ui, PORT, players, randomSubList(questions, numQuestions));
      ui.showMessage("Creating game");
      //by design, no other thread needed
      server.run();
    } catch (IOException e) {
      ui.showError("Could not create game: " + e.getMessage());
    }
  }

  /**
   * join a game as a client
   */
  private void joinGame() {
    try {
      var client = new Client(ui, PORT);
      //by design, no other thread needed
      client.run();
      ui.showMessage("joined game");
    } catch (IOException e) {
      ui.showError("Failed to join: " + e.getMessage());
    }
  }

  /**
   * get a random subset of the questions list
   * https://stackoverflow.com/questions/28582726/java-retrieve-a-random-discontinuous-sublist-of-an-arraylist-most-efficient-wa
   * @param list input list
   * @param newSize number of elements to take
   * @return random sublist of the input
   */
  public static List<Question> randomSubList(List<Question> list, int newSize) {
    list = new ArrayList<>(list);
    Collections.shuffle(list);
    return list.subList(0, newSize);
  }
}
