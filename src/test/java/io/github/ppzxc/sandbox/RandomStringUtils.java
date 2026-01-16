package io.github.ppzxc.sandbox;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 테스트에 사용할 랜덤 문자열을 생성하는 유틸리티 클래스입니다.
 */
public final class RandomStringUtils {

  /**
   * 랜덤 문자열 생성에 사용될 문자 셋 (영문 대소문자 및 숫자)
   */
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  private RandomStringUtils() {
  }

  /**
   * 지정된 길이의 랜덤 문자열을 생성합니다.
   *
   * @param length 생성할 문자열의 길이 (1 이상)
   * @return 생성된 랜덤 문자열
   * @throws IllegalArgumentException 길이가 1 미만인 경우 발생
   */
  public static String generateRandomString(int length) {
    if (length < 1) {
      throw new IllegalArgumentException("길이는 1 이상이어야 합니다.");
    }

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = ThreadLocalRandom.current().nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(randomIndex));
    }
    return sb.toString();
  }
}
