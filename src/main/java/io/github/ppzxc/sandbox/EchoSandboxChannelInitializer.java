package io.github.ppzxc.sandbox;

import io.github.ppzxc.sandbox.properties.InitializerProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.timeout.IdleStateEvent;
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
      java.util.Arrays.asList(new ByteBufLineEncoder(),
        new DelimiterBasedFrameDecoder(properties.getMaxFrameLength(), Delimiters.lineDelimiter()),
        new ChannelGatewayHandler(gateway),
        new SimpleChannelInboundHandler<ByteBuf>() {
          @Override
          protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // Input validation: Check message size
            int messageSize = msg.readableBytes();
            if (messageSize > properties.getMaxFrameLength()) {
              msg.release();
              ctx.close();
              return;
            }
            
            ByteBuf retainedMsg = msg.retain();
            ChannelFuture channelFuture = ctx.writeAndFlush(retainedMsg);
            channelFuture.addListener(future -> {
              if (!future.isSuccess()) {
                retainedMsg.release();
              }
            });
          }
        }));
  }
}
