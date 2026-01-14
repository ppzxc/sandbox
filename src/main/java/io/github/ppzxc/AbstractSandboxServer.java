package io.github.ppzxc;

import io.github.ppzxc.properties.SandboxServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.uring.IoUringIoHandler;
import io.netty.channel.uring.IoUringServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSandboxServer implements SandboxServer {

  private final SandboxServerProperties properties;
  private final EventLoopGroup parentGroup;
  private final EventLoopGroup childGroup;
  private final ServerBootstrap bootstrap;

  protected AbstractSandboxServer(SandboxServerProperties properties, ChannelInitializer<SocketChannel> initializer) {
    this.properties = properties;
    this.parentGroup = new MultiThreadIoEventLoopGroup(1, getHandlerFactory());
    this.childGroup = new MultiThreadIoEventLoopGroup(getHandlerFactory());
    this.bootstrap = new ServerBootstrap();
    this.bootstrap.group(parentGroup, childGroup)
      .channel(getChannelClass())
      .option(ChannelOption.SO_BACKLOG, properties.getServerOption().getBacklog())
      .option(ChannelOption.SO_REUSEADDR, properties.getServerOption().isReuseAddr())
      .childOption(ChannelOption.TCP_NODELAY, properties.getServerOption().isTcpNoDelay())
      .childOption(ChannelOption.SO_KEEPALIVE, properties.getServerOption().isKeepAlive())
      .childOption(ChannelOption.ALLOCATOR, properties.getServerOption().getAllocator())
      .childHandler(initializer);
  }

  private IoHandlerFactory getHandlerFactory() {
    switch (properties.getNativeTransport()) {
      case NIO:
        return NioIoHandler.newFactory();
      case EPOLL:
        return EpollIoHandler.newFactory();
      case IO_URING:
        return IoUringIoHandler.newFactory();
      default:
        throw new IllegalStateException("Unexpected value: " + properties.getNativeTransport());
    }
  }

  private Class<? extends ServerSocketChannel> getChannelClass() {
    switch (properties.getNativeTransport()) {
      case NIO:
        return NioServerSocketChannel.class;
      case EPOLL:
        return EpollServerSocketChannel.class;
      case IO_URING:
        return IoUringServerSocketChannel.class;
      default:
        throw new IllegalStateException("Unexpected value: " + properties.getNativeTransport());
    }
  }

  @Override
  public void startup() {
    try {
      ChannelFuture future = bootstrap.bind(properties.getHost(), properties.getPort()).sync();
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      log.error("Failed to start server", e);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void shutdownGracefully() {
    try {
      parentGroup.shutdownGracefully().sync();
    } catch (InterruptedException e) {
      log.error("Failed to shutdown parent group", e);
    }
    try {
      childGroup.shutdownGracefully().sync();
    } catch (InterruptedException e) {
      log.error("Failed to shutdown child group", e);
      Thread.currentThread().interrupt();
    }
  }
}
