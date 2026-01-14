package io.github.ppzxc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultIdleStateUserEventHandler extends SimpleUserEventChannelHandler<IdleStateEvent> implements
  IdleStateUserEventHandler {

  @Override
  protected void eventReceived(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
    if (log.isTraceEnabled()) {
      log.trace("channel={} event={}", ctx.channel(), evt);
    }
    if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
      ChannelFuture channelFuture = ctx.channel().closeFuture().sync();
      if (channelFuture.isSuccess()) {
        log.info("channel={} event={} message=closed", ctx.channel(), evt);
      } else {
        log.error("channel={} event={} message=failed close", ctx.channel(), evt, channelFuture.cause());
      }
    }
  }
}
