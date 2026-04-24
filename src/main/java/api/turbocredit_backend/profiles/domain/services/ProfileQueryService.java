package api.turbocredit_backend.profiles.domain.services;

import api.turbocredit_backend.profiles.domain.model.aggregates.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileQueryService {
    Optional<Profile> findById(UUID id);
    Optional<Profile> findByUserId(UUID userId);
}