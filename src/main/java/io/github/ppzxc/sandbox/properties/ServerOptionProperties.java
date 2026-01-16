package io.github.ppzxc.sandbox.properties;

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
  private int workerThreads = 0;
  private long quietPeriod = 2;
  private long shutdownTimeout = 15;
}
