package api.turbocredit_backend.profiles.domain.services;

import api.turbocredit_backend.profiles.domain.model.aggregates.Profile;
import api.turbocredit_backend.profiles.domain.model.valueobjects.Address;
import api.turbocredit_backend.profiles.domain.model.valueobjects.DocumentId;
import api.turbocredit_backend.profiles.domain.model.valueobjects.PhoneNumber;

import java.util.UUID;

public interface ProfileCommandService {
    Profile createProfile(UUID userId, String firstName, String lastName, DocumentId documentId);
    Profile updateProfile(UUID profileId, String firstName, String lastName, PhoneNumber phoneNumber);
    Profile updateAddress(UUID profileId, Address address);
    Profile updateFinancialInfo(UUID profileId, Double monthlyIncome, String employmentStatus);
}