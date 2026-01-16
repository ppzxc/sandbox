# Netty Sandbox

Netty 기반의 고성능 서버 애플리케이션 개발을 위한 샌드박스 라이브러리입니다. 채널 관리, 서버 초기화, 리소스 최적화 등 공통 기능을 추상화하여 제공합니다.

## 주요 기능

- **서버 추상화 (`AbstractSandboxServer`)**: Netty의 `ServerBootstrap`을 래핑하여 서버 시작/종료 로직을 간소화하였습니다.
- **채널 게이트웨이 (`ChannelGateway`)**: 연결된 클라이언트 채널들을 관리하고, 특정 채널로의 메시지 전송 및 브로드캐스트 기능을 제공합니다.
- **다양한 Native Transport 지원**: NIO, Epoll(Linux), IO_URING(Linux) 등 환경에 맞는 최적화된 전송 계층을 지원합니다.
- **리소스 최적화**: `ByteBufLineEncoder` 등을 통한 효율적인 메모리 사용 및 인코딩 처리를 수행합니다.
- **유연한 설정**: `SandboxServerProperties`를 통해 포트, 호스트, 스레드 모델, 타임아웃 등을 손쉽게 구성할 수 있습니다.

## 기술 스택

- **Java**: 8
- **Netty**: 4.1.130.Final
- **Lombok**: 1.18.42
- **SLF4J**: 1.7.36
- **Gradle**: Kotlin DSL

## 사용 방법

### 1. 서버 생성 및 구동

`DefaultSandboxServer`와 `EchoSandboxChannelInitializer`를 사용하여 간단한 에코 서버를 구성할 수 있습니다.

```java
SandboxServerProperties properties = new SandboxServerProperties();
DefaultChannelGateway gateway = new DefaultChannelGateway();
EchoSandboxChannelInitializer initializer = new EchoSandboxChannelInitializer(properties.getInitializer(), gateway);

DefaultSandboxServer server = new DefaultSandboxServer(properties, initializer);
server.startup();

// 서버 종료 시
// server.shutdownGracefully();
```

### 2. 채널 관리 및 메시지 전송

`ChannelGateway`를 통해 연결된 채널에 직접 메시지를 보내거나 모든 채널에 브로드캐스트할 수 있습니다.

```java
// 특정 채널에 전송
gateway.write(Unpooled.copiedBuffer("Hello", StandardCharsets.UTF_8), channelId);

// 모든 채널에 브로드캐스트
gateway.broadcast(Unpooled.copiedBuffer("Broadcast message", StandardCharsets.UTF_8));
```

## 프로젝트 구조

- `io.github.ppzxc`: 핵심 인터페이스 및 추상 클래스
- `io.github.ppzxc.domain`: 데이터 모델 (ChannelInfo, Result 등)
- `io.github.ppzxc.properties`: 서버 및 핸들러 설정 클래스

## 라이선스

이 프로젝트는 오픈 소스로 제공됩니다.
