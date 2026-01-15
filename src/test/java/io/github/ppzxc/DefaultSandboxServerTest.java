package io.github.ppzxc;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ppzxc.properties.SandboxServerProperties;
import io.netty.channel.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultSandboxServerTest {

  SandboxServerProperties properties;
  DefaultChannelGateway gateway;
  DefaultSandboxServer server;
  SimpleClient client;

  @BeforeEach
  void setUp() {
    properties = new SandboxServerProperties();
    gateway = new DefaultChannelGateway();
    EchoSandboxChannelInitializer initializer = new EchoSandboxChannelInitializer(properties.getInitializer(), gateway);
    server = new DefaultSandboxServer(properties, initializer);
    server.startup();
    client = new SimpleClient(properties.getHost(), properties.getPort());
  }

  @AfterEach
  void tearDown() {
    client.close();
    server.shutdownGracefully();
  }

  @DisplayName("channel registered test")
  @Test
  void t0() {
    // given
    String expected = RandomStringUtils.generateRandomString(100);

    // when
    client.sendMessage(expected);
    Channel actual = gateway.stream().findFirst()
      .orElseThrow(() -> new NullPointerException("channel not found"));

    // then
    assertThat(gateway.size()).isEqualTo(1);
    assertThat(gateway.exist(actual)).isTrue();
    assertThat(gateway.exist(actual.id().asShortText())).isTrue();
  }


  @DisplayName("Sends message and receives echo")
  @Test
  void t1() {
    // given
    String expected = RandomStringUtils.generateRandomString(100);

    // when
    String actual = client.sendMessage(expected);

    // then
    assertThat(actual).isEqualTo(expected);
  }
}