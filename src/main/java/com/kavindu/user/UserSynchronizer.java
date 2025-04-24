package com.kavindu.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void synchronizeWithIdp(Jwt token) {
        log.info("Synchronizing user with id");
        getUserEmail(token).ifPresent(userEmail -> {
            log.info("Synchronizing user with email {}", userEmail);
            Optional<User> optUSer = userRepository.findByEmail(userEmail);
            User user = userMapper.fromTokenAttributes(token.getClaims());
            optUSer.ifPresent(value -> user.setId(optUSer.get().getId()));

            userRepository.save(user);
        });


    }

    private Optional<String> getUserEmail (Jwt token) {
        Map<String,Object> attributes = token.getClaims();
        if (attributes.containsKey("email")){
            return Optional.of(attributes.get("email").toString());
        }
        return Optional.empty();
    }
}
