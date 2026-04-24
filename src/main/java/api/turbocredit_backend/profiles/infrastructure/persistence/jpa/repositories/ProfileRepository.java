package api.turbocredit_backend.profiles.infrastructure.persistence.jpa.repositories;

import api.turbocredit_backend.profiles.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}