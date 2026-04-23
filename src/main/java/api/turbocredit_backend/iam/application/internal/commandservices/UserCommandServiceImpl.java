package api.turbocredit_backend.iam.application.internal.commandservices;

import api.turbocredit_backend.iam.application.internal.outboundservices.hashing.HashingService;
import api.turbocredit_backend.iam.application.internal.outboundservices.tokens.TokenService;
import api.turbocredit_backend.iam.domain.model.aggregates.User;
import api.turbocredit_backend.iam.domain.model.commands.SignInCommand;
import api.turbocredit_backend.iam.domain.model.commands.SignUpCommand;
import api.turbocredit_backend.iam.domain.services.UserCommandService;
import api.turbocredit_backend.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import api.turbocredit_backend.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService,
                                  TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new RuntimeException("Email already registered");
        }

        var roles = command.roles().stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role.getName())))
                .toList();

        var user = new User(
                command.email(),
                hashingService.encode(command.password()),
                command.fullName(),
                roles
        );

        userRepository.save(user);
        return userRepository.findByEmail(command.email());
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!hashingService.matches(command.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
        var token = tokenService.generateToken(user.getEmail());
        return Optional.of(ImmutablePair.of(user, token));
    }
}