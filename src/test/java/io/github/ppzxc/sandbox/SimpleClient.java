package io.github.ppzxc.sandbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 테스트를 위해 서버에 연결하고 메시지를 주고받는 단순한 소켓 클라이언트입니다.
 */
public class SimpleClient {

  private final Socket socket;
  private final OutputStream bufferedWriter;
  private final BufferedReader bufferedReader;
  private final byte[] delimiter;

  /**
   * 지정된 호스트와 포트로 연결하며, 커스텀 구분자를 사용하는 클라이언트를 생성합니다.
   *
   * @param host      서버 호스트 주소
   * @param port      서버 포트 번호
   * @param delimiter 메시지 전송 시 사용할 구분자 (예: \r\n)
   */
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

  /**
   * 지정된 호스트와 포트로 연결하며, 기본 구분자(\r\n)를 사용하는 클라이언트를 생성합니다.
   *
   * @param host 서버 호스트 주소
   * @param port 서버 포트 번호
   */
  public SimpleClient(String host, int port) {
    this(host, port, new byte[]{'\r', '\n'});
  }

  /**
   * 서버로 메시지를 전송하고 한 줄의 응답을 읽어옵니다.
   *
   * @param msg 전송할 메시지 (구분자 제외)
   * @return 서버로부터 받은 한 줄의 응답 문자열
   */
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

  /**
   * 서버로부터 한 줄의 메시지를 읽어옵니다.
   *
   * @return 읽어온 문자열 (개행 문자 제외)
   */
  public String read() {
    try {
      return bufferedReader.readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 소켓 및 관련 스트림을 닫습니다.
   */
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