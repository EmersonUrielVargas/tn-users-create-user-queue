package co.com.nequi.sqs.listener.helper;

import co.com.nequi.model.user.User;
import co.com.nequi.sqs.listener.SQSProcessor;
import co.com.nequi.sqs.listener.config.SQSProperties;
import co.com.nequi.usecase.user.UserUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SQSListenerTest {

    @Mock
    private SqsAsyncClient asyncClient;

    @Mock
    private SQSProperties sqsProperties;

    @Mock
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        var message = Message.builder().body("{\"id\":12,\"name\":\"Emmanuel\"}").build();
        var deleteMessageResponse = DeleteMessageResponse.builder().build();
        var messageResponse = ReceiveMessageResponse.builder().messages(message).build();
        var messageResponseEmpty = ReceiveMessageResponse.builder().messages(List.of()).build();

        when(asyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(
                    CompletableFuture.completedFuture(messageResponse),
                    CompletableFuture.completedFuture(messageResponseEmpty)
                );
        when(asyncClient.deleteMessage(any(DeleteMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(deleteMessageResponse));
    }

    @Test
    void listenerTest() {
        var sqsListener = SQSListener.builder()
                .client(asyncClient)
                .properties(sqsProperties)
                .processor(new SQSProcessor(userUseCase))
                .operation("operation")
                .build();

        when(userUseCase.createUser(any(User.class))).thenReturn(Mono.empty());

        Flux<Void> flow = ReflectionTestUtils.invokeMethod(sqsListener, "listen");
        Assertions.assertNotNull(flow);
        StepVerifier.create(flow.take(1)).verifyComplete();
    }

}
