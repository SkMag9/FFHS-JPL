package network;

import core.Answer;
import core.Commands;
import core.Player;
import core.Question;
import core.UserInterface;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to handle a game as a server, which won't play it self
 */
public class Server {

  private final ExecutorService threadPool;
  private final int numPlayers;
  private final ServerSocket serverSocket;
  private final ClientHandler[] clients;
  private final List<Question> questions;
  private final UserInterface ui;
  private final ConcurrentLinkedQueue<Answer> answers;
  private CountDownLatch latch;

  /**
   * create a new game server
   * @param ui         helper object for UI output & input
   * @param port       on which port to listen for clients
   * @param numPlayers how many players are expected to join
   * @param questions  the questions for the game
   * @throws IOException if the socket can not be created
   */
  public Server(UserInterface ui, int port, int numPlayers, List<Question> questions)
      throws IOException {
    this.ui = ui;
    this.questions = questions;
    this.numPlayers = numPlayers;
    threadPool = Executors.newFixedThreadPool(numPlayers);
    clients = new ClientHandler[numPlayers];
    serverSocket = new ServerSocket(port);
    answers = new ConcurrentLinkedQueue<>();
  }

  /**
   * Contains the game logic from the server perspective
   */
  public void run() {
    try {
      //wait for players to join
      for (int i = 0; i < clients.length; i++) {
        clients[i] = new ClientHandler(serverSocket.accept(), this);
        threadPool.execute(clients[i]);
      }
      startCountDown();
      for (Question question : questions) {
        //always create a new countdown latch, because it can't be reused
        latch = new CountDownLatch(numPlayers);
        sendQuestion(question);
        latch.await();
        analyseResult(question);
        answers.clear();
      }

      sendLeaderBoard(clients);
      sendMessage("Thanks for playing and goodbye");
      endClientHandlers();
    } catch (IOException e) {
      ui.showError("Could not establish connections: " + e.getMessage());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Send a countdown to mark the start of the game
   * @throws InterruptedException if client connections can't be used correctly
   */
  private void startCountDown() throws InterruptedException {
    Thread.sleep(1000);
    sendMessage("All players have joined. Get ready, the game is starting in 3 seconds");
    Thread.sleep(1000);
    sendMessage("2...");
    Thread.sleep(1000);
    sendMessage("1...");
  }

  /**
   *  when all players responded, analyse and send the result
   * @param question the current question
   */
  private void analyseResult(Question question) {
    List<Answer> correctGuesses = answers.stream()
        .filter(x -> x.getAnswer() == question.correctAnswer).toList();
    Optional<Answer> winner = correctGuesses.stream().findFirst();
    if (winner.isPresent()) {
      winner.get().getPlayer().addPoints(1);
      sendResult(question, correctGuesses.size(), winner.get().getPlayer());
    } else {
      sendResult(question, 0, null);
    }
  }

  /**
   * sets each client handler to stop running
   */
  private void endClientHandlers() {
    //end client handler threads
    for (ClientHandler c : clients) {
      if (c != null) {
        c.end();
      }
    }
  }

  /**
   * Send the given string to all clients
   *
   * @param message the string to send
   */
  private void sendMessage(String message) {
    for (ClientHandler client : clients) {
      if (client == null) {
        continue;
      }
      client.sendMessage(message);
    }
    ui.showMessage(message);
  }

  /**
   * handle input from a client, is called by the client handlers
   *
   * @param command command to process
   * @param handler the origin of the message
   */
  public void handleClientInput(String command, ClientHandler handler) {
    if(command == null){
      return;
    }
    //Split the message, all are separated by a :
    String[] parts = command.split(":");
    switch (parts[0]) {
      case Commands.SET_NAME:
        //Set the name of the player
        handler.setName(parts[1]);
        sendMessage(String.format("Player %s joined", handler.getName()));
        break;
      case Commands.ANSWER:
        //Add the answer for the current round and count down the latch
        answers.add(new Answer(parts[1].charAt(0), handler));
        latch.countDown();
        break;
      default:
        break;
    }
  }

  /**
   * Send a question with its answer possibilities
   *
   * @param question the question to show
   */
  private void sendQuestion(Question question) {
    sendMessage(question.getQuestion());
    printAnswer('a', question.getAnswerA());
    printAnswer('b', question.getAnswerB());
    printAnswer('c', question.getAnswerC());
    sendMessage(Commands.GET_ANSWER);
  }

  /**
   * Prints the answer result for the given question
   *
   * @param question       the question which was asked
   * @param correctGuesses how many players got the correct answer
   * @param winner         which player was the fastest and won the round
   */
  private void sendResult(Question question, int correctGuesses, Player winner) {
    sendMessage(
        String.format("The correct answer was '%s' (%s), correctly guessed by %d players.%n",
            question.correctAnswer, question.getCorrectAnswerText(), correctGuesses));
    if (winner != null) {
      sendMessage(String.format("%s was the fastest and gets the point.%n", winner.getName()));
    }
  }

  /**
   * prints an answer possibility, removes the leading * if the answer is correct
   *
   * @param prefix the identification of the answer (a, b or c)
   * @param answer the answer itself
   */
  private void printAnswer(char prefix, String answer) {
    if (answer.startsWith("*")) {
      answer = answer.substring(1);
    }
    sendMessage(String.format("%s) %s%n", prefix, answer));
  }

  /**
   * send leader board to all clients
   *
   * @param players all players which shall be put into the ranking
   * @throws InterruptedException if sleep fails
   */
  private void sendLeaderBoard(Player[] players) throws InterruptedException {
    sendMessage("The game is finished...");
    sendMessage("And the results are...");
    List<Player> playersSorted = Arrays.stream(players)
        .sorted(Comparator.comparingInt(Player::getPoints).reversed()).toList();
    int previousPoints = -1;
    int previousRank = 0;
    for (int i = 0; i < players.length; i++) {
      //Add some delay to look more naturally
      Thread.sleep(2000);
      Player player = playersSorted.get(i);
      // if two or more players have the same number of points, they share the same rank
      // the next player will be placed accordingly on the next free rank (two player on first rank mean the third one will get rank 3)
      if (player.getPoints() != previousPoints) {
        previousRank = i + 1;
        previousPoints = player.getPoints();

      }
      sendMessage(String.format("%d. player %s (%d points)", previousRank, player.getName(),
          player.getPoints()));

    }
  }

  public UserInterface getUi() {
    return ui;
  }
}