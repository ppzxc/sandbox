package io.github.ppzxc;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;

public class DefaultChannelGateway extends AbstractChannelGateway {

  public DefaultChannelGateway(ChannelGroup channelGroup) {
    super(channelGroup);
  }

  public DefaultChannelGateway() {
    super();
  }

  @Override
  public ChannelMatcher channelMatcher(String id) {
    return channel -> channel.id().asShortText().equalsIgnoreCase(id);
  }
}
