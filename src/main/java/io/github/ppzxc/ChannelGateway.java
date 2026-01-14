package io.github.ppzxc;

import io.github.ppzxc.domain.Result;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import java.util.function.Consumer;

public interface ChannelGateway {

  void add(Channel channel);

  void write(Object message, String id);

  void write(Object message, String id, Consumer<Result> callback);

  void write(Object message, ChannelId channelId);

  void write(Object message, ChannelId channelId, Consumer<Result> callback);
}
