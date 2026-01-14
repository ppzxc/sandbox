package io.github.ppzxc;

import io.github.ppzxc.properties.InitializerProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public abstract class AbstractSandboxChannelInitializer extends ChannelInitializer<SocketChannel> {

  private final InitializerProperties properties;
  private final IdleStateUserEventHandler handler;

  protected AbstractSandboxChannelInitializer(InitializerProperties properties, IdleStateUserEventHandler handler) {
    this.properties = properties;
    this.handler = handler;
  }

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();
    pipeline.addLast(new LoggingHandler(properties.getLogLevel()));
    pipeline.addLast(getIdleStateHandler());
    pipeline.addLast(handler);
  }

  private IdleStateHandler getIdleStateHandler() {
    return new IdleStateHandler(properties.getIdleState().getReaderIdleTime(),
      properties.getIdleState().getWriterIdleTime(), properties.getIdleState().getAllIdleTime(),
      properties.getIdleState().getTimeUnit());
  }
}
