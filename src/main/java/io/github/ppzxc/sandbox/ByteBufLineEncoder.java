package io.github.ppzxc.sandbox;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class ByteBufLineEncoder extends MessageToByteEncoder<ByteBuf> {

  private static final ByteBuf LINE_SEPARATOR = Unpooled.unreleasableBuffer(
    Unpooled.copiedBuffer("\r\n", StandardCharsets.UTF_8));

  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
    out.writeBytes(msg);
    out.writeBytes(LINE_SEPARATOR.slice());
  }
}