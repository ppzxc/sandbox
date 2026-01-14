package io.github.ppzxc.properties;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdleStateProperties {

  private int readerIdleTime = 60;
  private int writerIdleTime = 0;
  private int allIdleTime = 0;
  private TimeUnit timeUnit = TimeUnit.SECONDS;
}
