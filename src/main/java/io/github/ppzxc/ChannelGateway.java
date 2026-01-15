package io.github.ppzxc;

import io.github.ppzxc.domain.ChannelInfo;
import io.github.ppzxc.domain.Result;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ChannelGateway {

  Stream<Channel> stream();

  int size();

  void add(Channel channel);

  boolean exist(Channel channel);

  boolean exist(String id);

  void write(Object message, String id);

  void write(Object message, String id, Consumer<Result> callback);

  void write(Object message, ChannelId channelId);

  void write(Object message, ChannelId channelId, Consumer<Result> callback);

  void broadcast(Object message);

  void broadcast(Object message, Consumer<Result> callback);

  void broadcastIf(Object message, Predicate<Channel> filter);

  void broadcastIf(Object message, Predicate<Channel> filter, Consumer<Result> callback);

  Optional<Channel> getChannel(String id);

  Optional<Channel> getChannel(ChannelId channelId);

  boolean remove(Channel channel);

  boolean remove(String id);

  boolean remove(ChannelId channelId);

  void disconnectAll();

  void disconnectAll(Consumer<Result> callback);

  Map<String, ChannelInfo> getChannelInfoMap();

  List<String> getChannelIds();
}
