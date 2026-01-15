package io.github.ppzxc;

import io.github.ppzxc.domain.Result;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractChannelGateway implements ChannelGateway {

  private final ChannelGroup channelGroup;

  protected AbstractChannelGateway(ChannelGroup channelGroup) {
    this.channelGroup = channelGroup;
  }

  protected AbstractChannelGateway() {
    this(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
  }

  public abstract ChannelMatcher channelMatcher(String id);

  @Override
  public Stream<Channel> stream() {
    return channelGroup.stream();
  }

  @Override
  public int size() {
    return channelGroup.size();
  }

  @Override
  public void add(Channel channel) {
    try {
      if (!channelGroup.add(channel)) {
        throw new IllegalStateException("channel add state is false");
      }
    } catch (Exception e) {
      log.error("id={} message=Failed to add channel", channel.id(), e);
      throw new IllegalArgumentException("Failed to add channel: " + channel.id(), e);
    }
  }

  @Override
  public boolean exist(Channel channel) {
    return channelGroup.stream().anyMatch(innerChannel -> innerChannel.equals(channel));
  }

  @Override
  public boolean exist(String id) {
    return channelGroup.stream().anyMatch(channel -> channel.id().asShortText().equalsIgnoreCase(id));
  }

  @Override
  public void write(Object message, String id) {
    write(message, channelMatcher(id), future -> {
      if (future.isSuccess()) {
        log.info("id={} message={} message=write success", id, message);
      } else {
        log.error("id={} message={} message=write fail", id, message, future.cause());
      }
    });
  }

  @Override
  public void write(Object message, String id, Consumer<Result> callback) {
    write(message, channelMatcher(id), future -> {
      if (future.isSuccess()) {
        callback.accept(Result.success());
      } else {
        callback.accept(Result.fail(future.cause()));
      }
    });
  }

  @Override
  public void write(Object message, ChannelId channelId) {
    write(message, channelMatcher(channelId.asShortText()), future -> {
      if (future.isSuccess()) {
        log.info("id={} message={} message=write success", channelId.asShortText(), message);
      } else {
        log.error("id={} message={} message=write fail", channelId.asShortText(), message, future.cause());
      }
    });
  }

  @Override
  public void write(Object message, ChannelId channelId, Consumer<Result> callback) {
    write(message, channelMatcher(channelId.asShortText()), future -> {
      if (future.isSuccess()) {
        callback.accept(Result.success());
      } else {
        callback.accept(Result.fail(future.cause()));
      }
    });
  }

  private void write(Object message, ChannelMatcher channelMatcher,
    GenericFutureListener<? extends Future<? super Void>> listener) {
    channelGroup.writeAndFlush(message, channelMatcher).addListener(listener);
  }
}
