package network;

import core.Commands;
import core.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * class representing a player on the server side and handling input from and to it
 */
public class ClientHandler extends Player implements Runnable {

  private final BufferedReader reader;
  private final PrintWriter writer;
  private final Server server;
  private boolean running = true;

  /**
   * create a new client handler
   * @param client client to handle
   * @param server parent server class running the game
   * @throws IOException if establishing the connections fails
   */
  public ClientHandler(Socket client, Server server) throws IOException {
    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    writer = new PrintWriter(client.getOutputStream(), true);
    this.server = server;
  }

  /**
   * end the handler
   */
  public void end() {
    sendMessage(Commands.END_GAME);
    this.running = false;
  }

  /**
   * wait for and handle input from a client
   */
  @Override
  public void run() {
    try {
      while (running) {
        String input = reader.readLine();
        server.handleClientInput(input, this);
      }
    } catch (IOException e) {
      server.getUi().showError(e.getMessage());
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        // already closed
      }
    }
  }

  /**
   * Send the given message over the socket to the client
   * @param message string to send
   */
  public void sendMessage(String message) {
    writer.println(message);
  }
}
