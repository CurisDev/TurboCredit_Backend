package api.turbocredit_backend.iam.domain.services;

import api.turbocredit_backend.iam.domain.model.aggregates.User;
import api.turbocredit_backend.iam.domain.model.commands.SignInCommand;
import api.turbocredit_backend.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
}