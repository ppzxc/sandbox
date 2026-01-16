package io.github.ppzxc.sandbox.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Result {

  private boolean success;
  private Throwable throwable;

  public static Result success() {
    return Result.builder()
      .success(true)
      .build();
  }

  public static Result fail(Throwable throwable) {
    return Result.builder()
      .success(false)
      .throwable(throwable)
      .build();
  }
}
