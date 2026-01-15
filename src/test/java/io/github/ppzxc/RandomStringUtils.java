package io.github.ppzxc;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomStringUtils {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  private RandomStringUtils() {
  }

  public static String generateRandomString(int length) {
    if (length < 1) {
      throw new IllegalArgumentException("길이는 1 이상이어야 합니다.");
    }

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      // CHARACTERS 문자열의 길이 내에서 랜덤한 인덱스 선택
      int randomIndex = ThreadLocalRandom.current().nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(randomIndex));
    }
    return sb.toString();
  }
}
