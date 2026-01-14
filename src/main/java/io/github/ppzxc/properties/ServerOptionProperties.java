package io.github.ppzxc.properties;

import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerOptionProperties {

  private int backlog = 4096;
  private boolean reuseAddr = true;
  private boolean tcpNoDelay = true;
  private boolean keepAlive = true;
  private PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
}
