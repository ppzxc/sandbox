package io.github.ppzxc;

import io.github.ppzxc.properties.InitializerProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.Arrays;
import java.util.List;

public class EchoSandboxChannelInitializer extends AbstractSandboxChannelInitializer {



  public EchoSandboxChannelInitializer(InitializerProperties properties,
    SimpleUserEventChannelHandler<IdleStateEvent> handler, List<ChannelHandler> handlers) {
    super(properties, handler, handlers);
  }

  public EchoSandboxChannelInitializer(InitializerProperties properties, List<ChannelHandler> handlers) {
    this(properties, new DefaultIdleStateUserEventHandler(), handlers);
  }

  public EchoSandboxChannelInitializer(InitializerProperties properties, ChannelGateway gateway) {
    this(properties, new DefaultIdleStateUserEventHandler(),
      Arrays.asList(new ByteBufLineEncoder(), new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()),
        new SimpleChannelInboundHandler<ByteBuf>() {
          @Override
          public void channelActive(ChannelHandlerContext ctx) throws Exception {
            gateway.add(ctx.channel());
            super.channelActive(ctx);
          }

          @Override
          public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
          }

          @Override
          protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            ctx.writeAndFlush(msg.retain());
          }
        }));
  }
}
