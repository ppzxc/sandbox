package io.github.ppzxc.sandbox;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ChannelGatewayHandler extends ChannelInboundHandlerAdapter {

  private final ChannelGateway gateway;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    gateway.add(ctx.channel());
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    gateway.remove(ctx.channel());
    super.channelInactive(ctx);
  }
}
