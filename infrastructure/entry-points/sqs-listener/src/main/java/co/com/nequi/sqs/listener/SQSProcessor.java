package co.com.nequi.sqs.listener;

import co.com.nequi.model.exceptions.UserException;
import co.com.nequi.model.user.User;
import co.com.nequi.sqs.listener.constants.SqsConstants;
import co.com.nequi.sqs.listener.util.SqsUtilities;
import co.com.nequi.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;
import java.util.function.Predicate;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final UserUseCase userUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Processing message: {}", message.body());
        return userUseCase.createUser(SqsUtilities.parserStringToObject(message.body(), User.class))
                .doOnSuccess(messageResponse -> log.info(SqsConstants.LOG_PROCESSED_MESSAGE_SUCCESSFUL))
                .doOnError(exception -> log.info(SqsConstants.LOG_PROCESSING_MESSAGE_ERROR,
                        kv(SqsConstants.LOG_RESPONSE_ERROR_KEY, exception)))
                .then();
    }
}
