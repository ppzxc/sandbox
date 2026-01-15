package io.github.ppzxc;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating and managing multiple test clients.
 */
public class MultiClient {

  private final List<SimpleClient> clients = new ArrayList<>();
  private final String host;
  private final int port;

  public MultiClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Creates the specified number of clients and connects them to the server.
   *
   * @param count the number of clients to create
   */
  public void createClients(int count) {
    for (int i = 0; i < count; i++) {
      clients.add(new SimpleClient(host, port));
    }
  }

  /**
   * Gets a client at the specified index.
   *
   * @param index the index of the client
   * @return the client at the specified index
   */
  public SimpleClient getClient(int index) {
    if (index < 0 || index >= clients.size()) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + clients.size());
    }
    return clients.get(index);
  }

  /**
   * Gets all clients.
   *
   * @return list of all clients
   */
  public List<SimpleClient> getClients() {
    return new ArrayList<>(clients);
  }

  /**
   * Closes all clients.
   */
  public void closeAll() {
    for (SimpleClient client : clients) {
      try {
        client.close();
      } catch (Exception e) {
      }
    }
    clients.clear();
  }

  /**
   * Returns the number of clients.
   *
   * @return the number of clients
   */
  public int size() {
    return clients.size();
  }
}
