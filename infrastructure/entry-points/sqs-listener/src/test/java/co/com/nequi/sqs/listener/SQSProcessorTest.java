package co.com.nequi.sqs.listener;

import co.com.nequi.model.user.User;
import co.com.nequi.usecase.user.UserUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SQSProcessorTest {

    private UserUseCase userUseCase;
    private SQSProcessor processor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userUseCase = mock(UserUseCase.class);
        processor = new SQSProcessor(userUseCase);
    }

    @Test
    void shouldProcessMessageSuccessfully() throws JsonProcessingException {
        User user = User.builder()
                .id(12L)
                .name("richard")
                .photoUrl("url")
                .email("test@test.com")
                .lastname("gomez").build();
        String bodyMessage = objectMapper.writeValueAsString(user);
        when(userUseCase.createUser(any(User.class))).thenReturn(Mono.just(user));

        Message message = Message.builder().body(bodyMessage).build();

        StepVerifier.create(processor.apply(message))
                .verifyComplete();

        verify(userUseCase, times(1)).createUser(any(User.class));
    }

    @Test
    void shouldHandleErrorWhenProcessingMessage() throws JsonProcessingException {

        User user = User.builder()
                .id(12L)
                .name("richard")
                .photoUrl("url")
                .email("test@test.com")
                .lastname("gomez").build();
        String bodyMessage = objectMapper.writeValueAsString(user);
        when(userUseCase.createUser(any(User.class))).thenReturn(Mono.error(new RuntimeException("error")));

        Message message = Message.builder().body(bodyMessage).build();

        StepVerifier.create(processor.apply(message))
                .verifyError();

        verify(userUseCase, times(1)).createUser(any(User.class));
    }
}