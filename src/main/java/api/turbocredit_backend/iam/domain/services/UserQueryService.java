package api.turbocredit_backend.iam.domain.services;

import api.turbocredit_backend.iam.domain.model.aggregates.User;
import api.turbocredit_backend.iam.domain.model.queries.GetAllUsersQuery;
import api.turbocredit_backend.iam.domain.model.queries.GetUserByEmailQuery;
import api.turbocredit_backend.iam.domain.model.queries.GetUserByIdQuery;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    List<User> handle(GetAllUsersQuery query);
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByEmailQuery query);
}