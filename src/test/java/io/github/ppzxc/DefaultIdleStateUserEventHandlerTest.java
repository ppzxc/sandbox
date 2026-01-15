package io.github.ppzxc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link DefaultIdleStateUserEventHandler}의 동작을 검증하기 위한 테스트 클래스입니다.
 * Netty의 {@link IdleStateEvent} 발생 시 채널이 올바르게 닫히는지 확인합니다.
 */
@ExtendWith(MockitoExtension.class)
class DefaultIdleStateUserEventHandlerTest {

    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private Channel channel;
    @Mock
    private ChannelFuture closeFuture;

    private DefaultIdleStateUserEventHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DefaultIdleStateUserEventHandler();
        lenient().when(ctx.channel()).thenReturn(channel);
        lenient().when(channel.close()).thenReturn(closeFuture);
    }

    /**
     * READER_IDLE_STATE_EVENT 발생 시 채널이 닫히는지 테스트합니다.
     */
    @Test
    void userEventTriggered_ReaderIdle_ClosesChannel() throws Exception {
        handler.userEventTriggered(ctx, IdleStateEvent.READER_IDLE_STATE_EVENT);

        verify(channel).close();
        verify(closeFuture).addListener(any(GenericFutureListener.class));
    }

    /**
     * WRITER_IDLE_STATE_EVENT 발생 시 채널이 닫히는지 테스트합니다.
     */
    @Test
    void userEventTriggered_WriterIdle_ClosesChannel() throws Exception {
        handler.userEventTriggered(ctx, IdleStateEvent.WRITER_IDLE_STATE_EVENT);

        verify(channel).close();
        verify(closeFuture).addListener(any(GenericFutureListener.class));
    }

    /**
     * ALL_IDLE_STATE_EVENT 발생 시 채널이 닫히는지 테스트합니다.
     */
    @Test
    void userEventTriggered_AllIdle_ClosesChannel() throws Exception {
        handler.userEventTriggered(ctx, IdleStateEvent.ALL_IDLE_STATE_EVENT);

        verify(channel).close();
        verify(closeFuture).addListener(any(GenericFutureListener.class));
    }

    /**
     * 유휴 상태 이벤트가 아닌 다른 이벤트 발생 시 채널이 닫히지 않는지 테스트합니다.
     */
    @Test
    void userEventTriggered_OtherEvent_DoesNotCloseChannel() throws Exception {
        Object otherEvent = new Object();

        handler.userEventTriggered(ctx, otherEvent);

        verify(channel, never()).close();
    }

    /**
     * 채널 종료 성공 시 로그가 올바르게 기록되는지 확인하기 위한 리스너 동작 테스트입니다.
     */
    @Test
    void listener_LogSuccess() throws Exception {
        handler.userEventTriggered(ctx, IdleStateEvent.ALL_IDLE_STATE_EVENT);

        ArgumentCaptor<GenericFutureListener<ChannelFuture>> captor = ArgumentCaptor.forClass(GenericFutureListener.class);
        verify(closeFuture).addListener(captor.capture());

        GenericFutureListener<ChannelFuture> listener = captor.getValue();
        when(closeFuture.isSuccess()).thenReturn(true);

        listener.operationComplete(closeFuture);
    }

    /**
     * 채널 종료 실패 시 에러 로그가 올바르게 기록되는지 확인하기 위한 리스너 동작 테스트입니다.
     */
    @Test
    void listener_LogFailure() throws Exception {
        handler.userEventTriggered(ctx, IdleStateEvent.ALL_IDLE_STATE_EVENT);

        ArgumentCaptor<GenericFutureListener<ChannelFuture>> captor = ArgumentCaptor.forClass(GenericFutureListener.class);
        verify(closeFuture).addListener(captor.capture());

        GenericFutureListener<ChannelFuture> listener = captor.getValue();
        when(closeFuture.isSuccess()).thenReturn(false);
        when(closeFuture.cause()).thenReturn(new RuntimeException("Close failed"));

        listener.operationComplete(closeFuture);
    }
}
