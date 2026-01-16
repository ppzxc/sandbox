package io.github.ppzxc.sandbox.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SandboxServerProperties {

  private String host = "0.0.0.0";
  private int port = 8080;
  private NativeTransport nativeTransport = NativeTransport.NIO;
  private ServerOptionProperties serverOption = new ServerOptionProperties();
  private InitializerProperties initializer = new InitializerProperties();
}
