package co.com.nequi.dynamodb.helper;

import co.com.nequi.dynamodb.DynamoDBTemplateAdapter;
import co.com.nequi.dynamodb.UserEntity;
import co.com.nequi.model.enums.DomainMessage;
import co.com.nequi.model.exceptions.TechnicalException;
import co.com.nequi.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<UserEntity> customerTable;

    private DynamoDBTemplateAdapter dynamoDBTemplateAdapter;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table("table_name", TableSchema.fromBean(UserEntity.class)))
                .thenReturn(customerTable);

        dynamoDBTemplateAdapter = new DynamoDBTemplateAdapter("table_name",dynamoDbEnhancedAsyncClient, mapper);

        userEntity = new UserEntity();
        userEntity.setId(12L);
        userEntity.setEmail("test@test.com");
        when(mapper.map(any(User.class), any())).thenReturn(userEntity);
        when(mapper.map(any(UserEntity.class), any())).thenReturn(builderUser());
    }

    @Test
    void modelEntityPropertiesMustNotBeNull() {
        UserEntity userEntityUnderTest = new UserEntity(12L, "luis",  "test@tesst.com", "url", "flores");

        assertNotNull(userEntityUnderTest.getId());
        assertNotNull(userEntityUnderTest.getEmail());
        assertNotNull(userEntityUnderTest.getName());
    }

    @Test
    void testSave() {
        when(customerTable.putItem(userEntity)).thenReturn(CompletableFuture.runAsync(()->{}));

        StepVerifier.create(dynamoDBTemplateAdapter.save(builderUser()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        Long userId = 12L;
        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().n(String.valueOf(userId)).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(builderUser());

        StepVerifier.create(dynamoDBTemplateAdapter.getById(userId))
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                        user.getName().equals("luis") &&
                        user.getEmail().equals("test@tesst.com") &&
                        user.getPhotoUrl().equals("url") &&
                        user.getLastname().equals("flores")
                )
                .verifyComplete();
    }

    @Test
    void testInsertUserSuccess() {
        when(customerTable.putItem(userEntity))
                .thenReturn(CompletableFuture.runAsync(()->{}));

        StepVerifier.create(dynamoDBTemplateAdapter.insertUser(builderUser()))
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                        user.getName().equals("luis") &&
                        user.getEmail().equals("test@tesst.com") &&
                        user.getPhotoUrl().equals("url") &&
                        user.getLastname().equals("flores")
                )
                .verifyComplete();
    }

    @Test
    void testInsertUserError() {
        when(customerTable.putItem(userEntity))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("fail")));

        StepVerifier.create(dynamoDBTemplateAdapter.insertUser(builderUser()))
                .expectErrorMatches(e -> {
                    assertInstanceOf(TechnicalException.class, e);
                    assertEquals(DomainMessage.INTERNAL_ERROR_PERSISTENCE_ADAPTER, ((TechnicalException) e).getDomainMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void testFindUserByIdSuccess() {
        Long userId = 12L;
        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().n(String.valueOf(userId)).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(userEntity));

        StepVerifier.create(dynamoDBTemplateAdapter.findUserById(userId))
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                        user.getName().equals("luis") &&
                        user.getEmail().equals("test@tesst.com") &&
                        user.getPhotoUrl().equals("url") &&
                        user.getLastname().equals("flores")
                )
                .verifyComplete();
    }

    @Test
    void testFindUserByIdError() {
        Long userId = 12L;
        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().n(String.valueOf(userId)).build()).build()))
                .thenReturn(CompletableFuture.failedFuture(new Exception("fail")));

        StepVerifier.create(dynamoDBTemplateAdapter.findUserById(userId))
                .expectErrorSatisfies(e -> {
                    assertInstanceOf(TechnicalException.class, e);
                    assertEquals(DomainMessage.INTERNAL_ERROR_PERSISTENCE_ADAPTER, ((TechnicalException) e).getDomainMessage());
                })
                .verify();
    }

    private User builderUser(){
        return new User(1L, "luis",  "test@tesst.com", "url", "flores");
    }
}