package io.github.ppzxc.sandbox.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents information about a connected channel.
 */
@Getter
@Builder
public class ChannelInfo {

  /**
   * Channel ID (short text format)
   */
  private final String channelId;

  /**
   * Remote address (client address)
   */
  private final String remoteAddress;

  /**
   * Local address (server address)
   */
  private final String localAddress;

  /**
   * Connection timestamp in milliseconds
   */
  private final long connectedTime;

  /**
   * Whether the channel is currently active
   */
  private final boolean active;

  /**
   * Whether the channel is currently writable
   */
  private final boolean writable;
}
