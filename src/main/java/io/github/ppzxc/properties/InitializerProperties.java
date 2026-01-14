package io.github.ppzxc.properties;

import io.netty.handler.logging.LogLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializerProperties {

  private LogLevel logLevel = LogLevel.INFO;
  private IdleStateProperties idleState = new IdleStateProperties();
}
