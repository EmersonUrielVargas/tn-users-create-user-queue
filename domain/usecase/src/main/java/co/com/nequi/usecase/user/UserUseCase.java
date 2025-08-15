package co.com.nequi.usecase.user;

import co.com.nequi.model.enums.DomainMessage;
import co.com.nequi.model.exceptions.BusinessException;
import co.com.nequi.model.exceptions.UserException;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserPersistenceGateway userPersistenceGateway;

    public Mono<User> createUser(User user){
        return userPersistenceGateway.findUserById(user.getId())
                .switchIfEmpty(
                        Mono.just(user)
                        .map(this::formatUser)
                        .flatMap(userToSave ->
                                Mono.defer(()->
                                        userPersistenceGateway.insertUser(userToSave)
                                        .switchIfEmpty(Mono.error(new BusinessException(DomainMessage.USER_CREATION_FAIL)))
                                )
                        )
                );
    }

    private User formatUser(User user) {
        return User.builder()
                .id(user.getId())
                .name(user.getName().toUpperCase())
                .email(user.getEmail().toUpperCase())
                .photoUrl(user.getPhotoUrl())
                .lastname(user.getLastname().toUpperCase())
                .build();
    }
}
