package co.com.nequi.usecase.user;

import co.com.nequi.model.enums.DomainMessage;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserPersistenceGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserPersistenceGateway userPersistenceGateway;

    @InjectMocks
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userUseCase = new UserUseCase(userPersistenceGateway);
    }

    @Nested
    @DisplayName("Create user tests")
    class CreateUserTest {

        @Test
        void testExistingUserInBd() {
            User user = buildUser();
            when(userPersistenceGateway.findUserById(12L)).thenReturn(Mono.just(user));

            StepVerifier.create(userUseCase.createUser(user))
                    .expectNextMatches(result ->
                            result.getId().equals(user.getId()) &&
                                    result.getName().equals(user.getName()) &&
                                    result.getEmail().equals(user.getEmail()))
                    .verifyComplete();
        }

        @Test
        void testCreateUserFormated() {
            User user = buildUser();

            User formattedUser = User.builder()
                    .id(12L)
                    .name("ANA")
                    .email("ANA@EMAIL.COM")
                    .photoUrl("url")
                    .lastname("PEREZ")
                    .build();

            when(userPersistenceGateway.findUserById(12L)).thenReturn(Mono.empty());
            when(userPersistenceGateway.insertUser(any(User.class))).thenReturn(Mono.just(formattedUser));

            StepVerifier.create(userUseCase.createUser(user))
                    .expectNextMatches(result ->
                            result.getId().equals(formattedUser.getId()) &&
                                    result.getName().equals("ANA") &&
                                    result.getEmail().equals("ANA@EMAIL.COM") &&
                                    result.getLastname().equals("PEREZ"))
                    .verifyComplete();
        }

        @Test
        void testErrorCreatingUserInBd() {
            User user = buildUser();

            when(userPersistenceGateway.findUserById(12L)).thenReturn(Mono.empty());
            when(userPersistenceGateway.insertUser(any(User.class))).thenReturn(Mono.empty());

            StepVerifier.create(userUseCase.createUser(user))
                    .expectErrorMatches(error -> {
                        Assertions.assertInstanceOf(BusinessException.class, error);
                        BusinessException ex = (BusinessException) error;
                        return ex.getDomainMessage().equals(DomainMessage.USER_CREATION_FAIL);
                    })
                    .verify();
        }
    }


    private User buildUser(){
        return User.builder()
                .id(12L)
                .name("Emmanuel")
                .lastname("casas")
                .email("test@test.com")
                .photoUrl("https://photo.url")
                .build();
    }

}
