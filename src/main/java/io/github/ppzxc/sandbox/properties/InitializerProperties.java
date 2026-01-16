package io.github.ppzxc.sandbox.properties;

import io.netty.handler.logging.LogLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitializerProperties {

  private LogLevel logLevel = LogLevel.INFO;
  private IdleStateProperties idleState = new IdleStateProperties();
  /**
   * Maximum frame length in bytes. Default is 64KB (65536 bytes).
   * Messages exceeding this limit will be rejected to prevent memory exhaustion.
   */
  private int maxFrameLength = 65536; // 64KB
}
