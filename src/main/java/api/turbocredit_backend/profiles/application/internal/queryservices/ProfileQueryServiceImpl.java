package api.turbocredit_backend.profiles.application.internal.queryservices;

import api.turbocredit_backend.profiles.domain.model.aggregates.Profile;
import api.turbocredit_backend.profiles.domain.services.ProfileQueryService;
import api.turbocredit_backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    @Override
    public Optional<Profile> findById(UUID id) {
        return profileRepository.findById(id);
    }

    @Override
    public Optional<Profile> findByUserId(UUID userId) {
        return profileRepository.findByUserId(userId);
    }
}