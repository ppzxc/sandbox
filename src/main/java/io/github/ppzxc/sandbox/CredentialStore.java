package io.github.ppzxc.sandbox;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Username/Password 기반 인증 정보를 저장하고 관리하는 클래스.
 * 테스트용 서버에서 클라이언트 인증을 위한 사용자 정보를 저장합니다.
 */
@Getter
@Setter
public class CredentialStore {

  /**
   * Username과 Password를 저장하는 Map.
   * Key: Username, Value: Password
   */
  private Map<String, String> credentials = new HashMap<>();

  /**
   * 사용자 인증 정보를 추가합니다.
   *
   * @param username 사용자명
   * @param password 비밀번호
   */
  public void addCredential(String username, String password) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    credentials.put(username.trim(), password);
  }

  /**
   * 사용자 인증 정보를 제거합니다.
   *
   * @param username 제거할 사용자명
   * @return 제거 성공 여부
   */
  public boolean removeCredential(String username) {
    if (username == null) {
      return false;
    }
    return credentials.remove(username.trim()) != null;
  }

  /**
   * 사용자 인증 정보가 존재하는지 확인합니다.
   *
   * @param username 확인할 사용자명
   * @return 존재 여부
   */
  public boolean hasCredential(String username) {
    return username != null && credentials.containsKey(username.trim());
  }

  /**
   * 사용자명과 비밀번호가 일치하는지 확인합니다.
   *
   * @param username 사용자명
   * @param password 비밀번호
   * @return 인증 성공 여부
   */
  public boolean validate(String username, String password) {
    if (username == null || password == null) {
      return false;
    }
    String storedPassword = credentials.get(username.trim());
    return storedPassword != null && storedPassword.equals(password);
  }

  /**
   * 모든 인증 정보를 Map으로 반환합니다.
   *
   * @return 인증 정보 Map의 복사본
   */
  public Map<String, String> getAllCredentials() {
    return new HashMap<>(credentials);
  }

  /**
   * 모든 인증 정보를 제거합니다.
   */
  public void clearCredentials() {
    credentials.clear();
  }

  /**
   * 저장된 인증 정보의 개수를 반환합니다.
   *
   * @return 인증 정보 개수
   */
  public int getCredentialCount() {
    return credentials.size();
  }
}
