package ru.app.apigateway.repository;

import reactor.core.publisher.Mono;
import ru.app.apigateway.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByEmail(String email);
    Mono<User> findByUsername(String username);
}