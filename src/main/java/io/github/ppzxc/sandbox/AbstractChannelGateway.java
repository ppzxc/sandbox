package io.github.ppzxc.sandbox;

import io.github.ppzxc.sandbox.domain.ChannelInfo;
import io.github.ppzxc.sandbox.domain.Result;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractChannelGateway implements ChannelGateway {

  private final ChannelGroup channelGroup;
  private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

  protected AbstractChannelGateway(ChannelGroup channelGroup) {
    this.channelGroup = channelGroup;
  }

  protected AbstractChannelGateway() {
    this(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
  }

  public abstract ChannelMatcher channelMatcher(String id);

  @Override
  public Stream<Channel> stream() {
    return channelMap.values().stream();
  }

  @Override
  public int size() {
    return channelGroup.size();
  }

  @Override
  public void add(Channel channel) {
    if (channel == null) {
      return;
    }
    channelGroup.add(channel);
    channelMap.put(channel.id().asShortText(), channel);
    log.debug("id={} message=channel added", channel.id());
  }

  @Override
  public boolean exist(Channel channel) {
    return channelMap.containsKey(channel.id().asShortText());
  }

  @Override
  public boolean exist(String id) {
    return channelMap.containsKey(id);
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
      try {
        if (future.isSuccess()) {
          callback.accept(Result.success());
        } else {
          callback.accept(Result.fail(future.cause()));
        }
      } catch (Exception e) {
        log.error("id={} message={} message=callback execution failed", id, message, e);
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
      try {
        if (future.isSuccess()) {
          callback.accept(Result.success());
        } else {
          callback.accept(Result.fail(future.cause()));
        }
      } catch (Exception e) {
        log.error("id={} message={} message=callback execution failed", channelId.asShortText(), message, e);
      }
    });
  }

  private void write(Object message, ChannelMatcher channelMatcher,
    GenericFutureListener<? extends Future<? super Void>> listener) {
    channelGroup.writeAndFlush(message, channelMatcher).addListener(listener);
  }

  @Override
  public void broadcast(Object message) {
    channelGroup.writeAndFlush(message).addListener(future -> {
      if (future.isSuccess()) {
        log.info("message={} message=broadcast success", message);
      } else {
        log.error("message={} message=broadcast fail", message, future.cause());
      }
    });
  }

  @Override
  public void broadcast(Object message, Consumer<Result> callback) {
    channelGroup.writeAndFlush(message).addListener(future -> {
      try {
        if (future.isSuccess()) {
          callback.accept(Result.success());
        } else {
          callback.accept(Result.fail(future.cause()));
        }
      } catch (Exception e) {
        log.error("message={} message=broadcast callback execution failed", message, e);
      }
    });
  }

  @Override
  public void broadcastIf(Object message, Predicate<Channel> filter) {
    ChannelMatcher matcher = filter::test;
    channelGroup.writeAndFlush(message, matcher).addListener(future -> {
      if (future.isSuccess()) {
        log.info("message={} message=filtered broadcast success", message);
      } else {
        log.error("message={} message=filtered broadcast fail", message, future.cause());
      }
    });
  }

  @Override
  public void broadcastIf(Object message, Predicate<Channel> filter, Consumer<Result> callback) {
    ChannelMatcher matcher = filter::test;
    channelGroup.writeAndFlush(message, matcher).addListener(future -> {
      try {
        if (future.isSuccess()) {
          callback.accept(Result.success());
        } else {
          callback.accept(Result.fail(future.cause()));
        }
      } catch (Exception e) {
        log.error("message={} message=filtered broadcast callback execution failed", message, e);
      }
    });
  }

  @Override
  public Optional<Channel> getChannel(String id) {
    return Optional.ofNullable(channelMap.get(id));
  }

  @Override
  public Optional<Channel> getChannel(ChannelId channelId) {
    return getChannel(channelId.asShortText());
  }

  @Override
  public boolean remove(Channel channel) {
    if (channel == null) {
      return false;
    }
    String shortId = channel.id().asShortText();
    boolean removedFromGroup = channelGroup.remove(channel);
    Channel removedFromMap = channelMap.remove(shortId);
    log.debug("id={} message=channel removed, group={}, map={}", shortId, removedFromGroup, removedFromMap != null);
    return removedFromGroup || removedFromMap != null;
  }

  @Override
  public boolean remove(String id) {
    if (id == null) {
      return false;
    }
    Channel channel = channelMap.get(id);
    return remove(channel);
  }

  @Override
  public boolean remove(ChannelId channelId) {
    return remove(channelId.asShortText());
  }

  @Override
  public void disconnectAll() {
    channelMap.clear();
    ChannelGroupFuture future = channelGroup.close();
    future.addListener(f -> {
      if (f.isSuccess()) {
        log.info("message=all channels disconnected successfully");
      } else {
        log.error("message=failed to disconnect all channels", f.cause());
      }
    });
  }

  @Override
  public void disconnectAll(Consumer<Result> callback) {
    channelMap.clear();
    ChannelGroupFuture future = channelGroup.close();
    future.addListener(f -> {
      try {
        if (f.isSuccess()) {
          callback.accept(Result.success());
        } else {
          callback.accept(Result.fail(f.cause()));
        }
      } catch (Exception e) {
        log.error("message=disconnectAll callback execution failed", e);
      }
    });
  }

  @Override
  public Map<String, ChannelInfo> getChannelInfoMap() {
    return channelMap.values().stream()
      .map(channel -> ChannelInfo.builder()
        .channelId(channel.id().asShortText())
        .remoteAddress(channel.remoteAddress() != null ? channel.remoteAddress().toString() : "unknown")
        .localAddress(channel.localAddress() != null ? channel.localAddress().toString() : "unknown")
        .connectedTime(System.currentTimeMillis())
        .active(channel.isActive())
        .writable(channel.isWritable())
        .build())
      .collect(Collectors.toMap(ChannelInfo::getChannelId, info -> info));
  }

  @Override
  public List<String> getChannelIds() {
    return new java.util.ArrayList<>(channelMap.keySet());
  }
}
