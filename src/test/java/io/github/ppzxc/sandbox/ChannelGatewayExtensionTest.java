package io.github.ppzxc.sandbox;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ppzxc.sandbox.domain.ChannelInfo;
import io.github.ppzxc.sandbox.properties.SandboxServerProperties;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChannelGatewayExtensionTest {

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

  @DisplayName("브로드캐스트 테스트 - 단일 클라이언트가 브로드캐스트된 메시지를 수신함")
  @Test
  void testBroadcast() {
    String initMessage = RandomStringUtils.generateRandomString(50);
    String broadcastMessage = RandomStringUtils.generateRandomString(50);

    String echo = client.send(initMessage);
    assertThat(echo).isEqualTo(initMessage);
    assertThat(gateway.size()).isEqualTo(1);

    gateway.broadcast(Unpooled.wrappedBuffer(broadcastMessage.getBytes(StandardCharsets.UTF_8)));

    String received = client.read();
    assertThat(received).isEqualTo(broadcastMessage);
  }

  @DisplayName("채널 조회 테스트 - ID를 사용하여 채널을 조회함")
  @Test
  void testGetChannel() {
    String echo = client.send("TEST");
    assertThat(echo).isEqualTo("TEST");

    String channelId = gateway.getChannelIds().get(0);
    Optional<Channel> channel = gateway.getChannel(channelId);

    assertThat(channel).isPresent();
    assertThat(channel.get().id().asShortText()).isEqualToIgnoringCase(channelId);
  }

  @DisplayName("채널 조회 테스트 - 존재하지 않는 채널 ID로 조회 시 빈 결과를 반환함")
  @Test
  void testGetChannelNotFound() {
    Optional<Channel> channel = gateway.getChannel("non-existent-id");

    assertThat(channel).isEmpty();
  }

  @DisplayName("채널 제거 테스트 - 게이트웨이에서 채널이 정상적으로 제거됨")
  @Test
  void testRemoveChannel() {
    client.send("CONNECT");
    assertThat(gateway.size()).isEqualTo(1);

    String channelId = gateway.getChannelIds().get(0);
    boolean removed = gateway.remove(channelId);

    assertThat(removed).isTrue();
    assertThat(gateway.size()).isEqualTo(0);
    assertThat(gateway.getChannel(channelId)).isEmpty();
  }

  @DisplayName("채널 제거 테스트 - 존재하지 않는 채널 제거 시도 시 false를 반환함")
  @Test
  void testRemoveChannelNotFound() {
    boolean removed = gateway.remove("non-existent-id");

    assertThat(removed).isFalse();
  }

  @DisplayName("채널 정보 맵 조회 테스트 - 연결된 채널의 상세 정보를 조회함")
  @Test
  void testGetChannelInfoMap() {
    client.send("HELLO");
    assertThat(gateway.size()).isEqualTo(1);

    Map<String, ChannelInfo> infoMap = gateway.getChannelInfoMap();

    assertThat(infoMap).hasSize(1);
    ChannelInfo info = infoMap.values().iterator().next();
    assertThat(info.getChannelId()).isNotEmpty();
    assertThat(info.getRemoteAddress()).isNotEmpty();
    assertThat(info.getLocalAddress()).isNotEmpty();
    assertThat(info.isActive()).isTrue();
  }

  @DisplayName("채널 ID 목록 조회 테스트 - 게이트웨이에 등록된 모든 채널의 ID 목록을 조회함")
  @Test
  void testGetChannelIds() {
    client.send("CONNECT");
    assertThat(gateway.size()).isEqualTo(1);

    List<String> channelIds = gateway.getChannelIds();

    assertThat(channelIds).hasSize(1);
    String id = channelIds.get(0);
    assertThat(id).isNotEmpty();
    assertThat(gateway.exist(id)).isTrue();
  }
}
