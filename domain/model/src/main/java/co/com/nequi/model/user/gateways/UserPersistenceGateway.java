package co.com.nequi.model.user.gateways;

import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;

public interface UserPersistenceGateway {

    Mono<User> insertUser(User user);
    Mono<User> findUserById(Long userId);
}
