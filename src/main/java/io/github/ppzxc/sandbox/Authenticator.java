package io.github.ppzxc.sandbox;

import io.netty.channel.Channel;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 단순한 인증 검증 유틸리티 클래스.
 * CredentialStore에 저장된 username/password를 기반으로 인증 여부를 검사하고,
 * 인증된 사용자의 로그인 상태를 관리합니다.
 */
public class Authenticator {

  private final CredentialStore credentialStore;
  /**
   * Channel과 인증된 username을 매핑하는 Map.
   * Key: Channel, Value: Username
   */
  private final Map<Channel, String> authenticatedChannels = new ConcurrentHashMap<>();

  public Authenticator(CredentialStore credentialStore) {
    this.credentialStore = Objects.requireNonNull(credentialStore, "CredentialStore cannot be null");
  }

  /**
   * username과 password로 인증을 수행하고, 성공 시 로그인 상태로 유지합니다.
   *
   * @param channel 인증할 Channel
   * @param username 사용자명
   * @param password 비밀번호
   * @return 인증 성공 여부
   */
  public boolean authenticate(Channel channel, String username, String password) {
    if (channel == null) {
      return false;
    }
    if (username == null || password == null) {
      return false;
    }
    
    // 인증 검증
    if (credentialStore.validate(username, password)) {
      // 인증 성공 시 로그인 상태로 저장
      authenticatedChannels.put(channel, username.trim());
      return true;
    }
    return false;
  }

  /**
   * Channel의 인증 상태를 해제합니다 (로그아웃).
   *
   * @param channel 로그아웃할 Channel
   * @return 로그아웃 성공 여부 (이미 로그아웃된 경우 false)
   */
  public boolean logout(Channel channel) {
    if (channel == null) {
      return false;
    }
    return authenticatedChannels.remove(channel) != null;
  }

  /**
   * Channel이 인증되어 있는지 확인합니다.
   *
   * @param channel 확인할 Channel
   * @return 인증 여부
   */
  public boolean isAuthenticated(Channel channel) {
    if (channel == null) {
      return false;
    }
    return authenticatedChannels.containsKey(channel);
  }

  /**
   * Channel에 연결된 인증된 username을 반환합니다.
   *
   * @param channel 확인할 Channel
   * @return 인증된 username, 인증되지 않은 경우 null
   */
  public String getAuthenticatedUsername(Channel channel) {
    if (channel == null) {
      return null;
    }
    return authenticatedChannels.get(channel);
  }

  /**
   * username이 등록되어 있는지 확인합니다.
   *
   * @param username 확인할 사용자명
   * @return 등록 여부
   */
  public boolean hasUser(String username) {
    return credentialStore.hasCredential(username);
  }

  /**
   * 현재 로그인된 모든 Channel의 개수를 반환합니다.
   *
   * @return 로그인된 Channel 개수
   */
  public int getAuthenticatedChannelCount() {
    return authenticatedChannels.size();
  }
}
