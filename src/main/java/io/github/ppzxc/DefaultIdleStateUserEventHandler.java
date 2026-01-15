package io.github.ppzxc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultIdleStateUserEventHandler extends SimpleUserEventChannelHandler<IdleStateEvent> {

  @Override
  protected void eventReceived(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
    if (log.isTraceEnabled()) {
      log.trace("channel={} event={}", ctx.channel(), evt);
    }
    if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT || evt == IdleStateEvent.WRITER_IDLE_STATE_EVENT
      || evt == IdleStateEvent.ALL_IDLE_STATE_EVENT) {
      ctx.channel().close().addListener(future -> {
        if (future.isSuccess()) {
          log.info("channel={} event={} message=closed", ctx.channel(), evt);
        } else {
          log.error("channel={} event={} message=failed close", ctx.channel(), evt, future.cause());
        }
      });
    }
  }
}
