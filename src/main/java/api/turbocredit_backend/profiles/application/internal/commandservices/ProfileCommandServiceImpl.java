package api.turbocredit_backend.profiles.application.internal.commandservices;

import api.turbocredit_backend.profiles.domain.exceptions.ProfileNotFoundException;
import api.turbocredit_backend.profiles.domain.model.aggregates.Profile;
import api.turbocredit_backend.profiles.domain.model.valueobjects.Address;
import api.turbocredit_backend.profiles.domain.model.valueobjects.DocumentId;
import api.turbocredit_backend.profiles.domain.model.valueobjects.PhoneNumber;
import api.turbocredit_backend.profiles.domain.services.ProfileCommandService;
import api.turbocredit_backend.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    @Override
    public Profile createProfile(UUID userId, String firstName, String lastName, DocumentId documentId) {
        Profile profile = new Profile(userId, firstName, lastName, documentId);
        return profileRepository.save(profile);
    }

    @Override
    public Profile updateProfile(UUID profileId, String firstName, String lastName, PhoneNumber phoneNumber) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId.toString()));
        profile.updatePersonalInfo(firstName, lastName, phoneNumber);
        return profileRepository.save(profile);
    }

    @Override
    public Profile updateAddress(UUID profileId, Address address) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId.toString()));
        profile.updateAddress(address);
        return profileRepository.save(profile);
    }

    @Override
    public Profile updateFinancialInfo(UUID profileId, Double monthlyIncome, String employmentStatus) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId.toString()));
        profile.updateFinancialInfo(monthlyIncome, employmentStatus);
        return profileRepository.save(profile);
    }
}