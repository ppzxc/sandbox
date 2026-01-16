package io.github.ppzxc.sandbox;

import io.github.ppzxc.sandbox.properties.SandboxServerProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class DefaultSandboxServer extends AbstractSandboxServer {

  public DefaultSandboxServer(SandboxServerProperties properties, ChannelInitializer<SocketChannel> initializer) {
    super(properties, initializer);
  }
}
