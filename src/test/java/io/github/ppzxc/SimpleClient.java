package io.github.ppzxc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SimpleClient {

  private final Socket socket;
  private final OutputStream bufferedWriter;
  private final BufferedReader bufferedReader;
  private final byte[] delimiter;

  public SimpleClient(String host, int port, byte[] delimiter) {
    try {
      this.socket = new Socket(host, port);
      this.bufferedWriter = socket.getOutputStream();
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.delimiter = delimiter;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public SimpleClient(String host, int port) {
    this(host, port, new byte[]{'\r', '\n'});
  }

  public String send(String msg) {
    try {
      byte[] first = msg.getBytes(StandardCharsets.UTF_8);
      byte[] combined = new byte[first.length + delimiter.length];
      System.arraycopy(first, 0, combined, 0, first.length);
      System.arraycopy(delimiter, 0, combined, first.length, delimiter.length);
      bufferedWriter.write(combined);
      bufferedWriter.flush();
      return read();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String read() {
    try {
      return bufferedReader.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    try {
      bufferedReader.close();
      bufferedWriter.close();
      socket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}