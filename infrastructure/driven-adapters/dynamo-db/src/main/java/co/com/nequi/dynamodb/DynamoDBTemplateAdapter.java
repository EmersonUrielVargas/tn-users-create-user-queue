package co.com.nequi.dynamodb;

import co.com.nequi.dynamodb.constants.DynamoDbConstants;
import co.com.nequi.dynamodb.helper.TemplateAdapterOperations;
import co.com.nequi.model.enums.DomainMessage;
import co.com.nequi.model.exceptions.TechnicalException;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserPersistenceGateway;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<User, Long, UserEntity> implements UserPersistenceGateway {

    public DynamoDBTemplateAdapter(@Value("${aws.dynamodb.users-table}") String tableName,
            DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class), tableName);
    }

    @Override
    public Mono<User> insertUser(User user) {
        return super.save(user)
                .doOnSuccess(messageResponse -> log.info(DynamoDbConstants.LOG_CREATE_NEW_USER_SUCCESSFUL,
                        kv(DynamoDbConstants.LOG_RESPONSE_CREATE_USER_SUCCESSFUL_KEY, messageResponse)))
                .doOnError(exception -> log.info(DynamoDbConstants.LOG_CREATE_NEW_USER_ERROR,
                        kv(DynamoDbConstants.LOG_RESPONSE_CREATE_USER_ERROR_KEY,
                        exception)))
                .onErrorMap(exception -> new TechnicalException(exception, DomainMessage.INTERNAL_ERROR_PERSISTENCE_ADAPTER));
    }

    @Override
    public Mono<User> findUserById(Long userId) {
        return super.getById(userId)
                .doOnSuccess(messageResponse -> log.info(DynamoDbConstants.LOG_GET_USER_SUCCESSFUL,
                        kv(DynamoDbConstants.LOG_RESPONSE_GET_USER_SUCCESSFUL_KEY, messageResponse)))
                .doOnError(exception -> log.info(DynamoDbConstants.LOG_GET_USER_ERROR, kv(DynamoDbConstants.LOG_RESPONSE_GET_USER_ERROR_KEY,
                        exception)))
                .onErrorMap(exception -> new TechnicalException(exception, DomainMessage.INTERNAL_ERROR_PERSISTENCE_ADAPTER));
    }
}
