package io.github.ppzxc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.StandardCharsets;

public class ByteBufLineEncoder extends MessageToByteEncoder<ByteBuf> {

  private final byte[] lineSeparator = "\r\n".getBytes(StandardCharsets.UTF_8);

  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
    out.writeBytes(msg);
    out.writeBytes(lineSeparator);
  }
}