package io.github.ppzxc.sandbox;

import io.github.ppzxc.sandbox.properties.InitializerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.List;

public abstract class AbstractSandboxChannelInitializer extends ChannelInitializer<SocketChannel> {

  private final InitializerProperties properties;
  private final SimpleUserEventChannelHandler<IdleStateEvent> handler;
  private final List<ChannelHandler> handlers;

  protected AbstractSandboxChannelInitializer(InitializerProperties properties,
    SimpleUserEventChannelHandler<IdleStateEvent> handler, List<ChannelHandler> handlers) {
    this.properties = properties;
    this.handler = handler;
    this.handlers = handlers;
  }

  @Override
  protected void initChannel(SocketChannel socketChannel) throws Exception {
    ChannelPipeline pipeline = socketChannel.pipeline();
    pipeline.addLast(new LoggingHandler(properties.getLogLevel()));
    pipeline.addLast(getIdleStateHandler());
    pipeline.addLast(handler);
    handlers.forEach(pipeline::addLast);
  }

  private IdleStateHandler getIdleStateHandler() {
    return new IdleStateHandler(properties.getIdleState().getReaderIdleTime(),
      properties.getIdleState().getWriterIdleTime(), properties.getIdleState().getAllIdleTime(),
      properties.getIdleState().getTimeUnit());
  }
}
