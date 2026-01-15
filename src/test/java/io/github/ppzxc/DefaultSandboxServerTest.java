package io.github.ppzxc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.github.ppzxc.properties.SandboxServerProperties;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultSandboxServer}의 기능을 검증하는 테스트 클래스입니다.
 * 실제 서버를 구동하고 {@link SimpleClient}를 사용하여 통신을 테스트합니다.
 */
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

  /**
   * 클라이언트가 연결되었을 때 게이트웨이에 채널이 정상적으로 등록되는지 테스트합니다.
   */
  @DisplayName("channel registered test")
  @Test
  void t0() {
    String expected = RandomStringUtils.generateRandomString(100);

    client.send(expected);

    await().atMost(Duration.ofSeconds(5)).until(() -> gateway.size() == 1);

    Channel actual = gateway.stream().findFirst()
      .orElseThrow(() -> new NullPointerException("channel not found"));

    assertThat(gateway.size()).isEqualTo(1);
    assertThat(gateway.exist(actual)).isTrue();
    assertThat(gateway.exist(actual.id().asShortText())).isTrue();
  }

  /**
   * 클라이언트가 보낸 메시지를 서버가 그대로 다시 보내는지(Echo) 테스트합니다.
   */
  @DisplayName("Sends message and receives echo")
  @Test
  void t1() {
    String expected = RandomStringUtils.generateRandomString(100);

    String actual = client.send(expected);

    assertThat(actual).isEqualTo(expected);
  }

  /**
   * 서버 측에서 특정 채널로 메시지를 직접 전송할 수 있는지 테스트합니다.
   */
  @DisplayName("server side send message")
  @Test
  void t2() {
    String firstExpected = RandomStringUtils.generateRandomString(100);
    String expected = RandomStringUtils.generateRandomString(100);

    String firstActual = client.send(firstExpected);
    gateway.stream().findFirst().ifPresent(channel -> gateway.write(Unpooled.wrappedBuffer(expected.getBytes(
      StandardCharsets.UTF_8)), channel.id().asShortText()));
    String actual = client.read();

    assertThat(firstActual).isEqualTo(firstExpected);
    assertThat(actual).isEqualTo(expected);
  }
}