package network;

import core.Commands;
import core.Player;
import core.UserInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Game client Connects to a server via sockets and handles interaction with a player
 * Extends player class to represent it on the client side
 */
public class Client extends Player implements Runnable {

  private final UserInterface ui;
  private final Socket socket;
  private final BufferedReader reader;
  private final PrintWriter writer;

  /**
   * Creates a new Client and connects to the server
   * @param ui   UserInterface Helper object
   * @param port Port to establish connection on
   * @throws IOException if establishing the connection fails
   */
  public Client(UserInterface ui, int port) throws IOException {
    this.ui = ui;
    String ip = ui.getServerIP();
    setName(ui.getName());
    socket = new Socket(ip, port);
    writer = new PrintWriter(socket.getOutputStream(), true);
    writer.println(Commands.SET_NAME + ":" + getName());
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  public void run() {
    boolean running = true;
    try {
      while (running) {
        String serverMessage = reader.readLine();
        switch (serverMessage) {
          case Commands.GET_ANSWER -> {
            char input = ui.getAnswer();
            writer.println(String.format("%s:%s", Commands.ANSWER, input));
          }
          case Commands.END_GAME -> {
            running = false;
            writer.println(Commands.END_GAME);
          }
          default -> ui.showMessage(serverMessage);
        }
      }
    } catch (IOException e) {
      ui.showError("Connection failed");
    } finally {
      try {
        reader.close();
        writer.close();
        socket.close();
      } catch (IOException e) {
        //already closed
      }
    }
  }
}
