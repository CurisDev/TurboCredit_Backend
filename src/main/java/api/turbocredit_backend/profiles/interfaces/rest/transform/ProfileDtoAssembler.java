package api.turbocredit_backend.profiles.interfaces.rest.transform;

import api.turbocredit_backend.profiles.domain.model.aggregates.Profile;
import api.turbocredit_backend.profiles.interfaces.rest.resources.ProfileResource;

public class ProfileDtoAssembler {

    public static ProfileResource toResource(Profile profile) {
        ProfileResource resource = new ProfileResource();
        resource.setId(profile.getId());
        resource.setUserId(profile.getUserId());
        resource.setFirstName(profile.getFirstName());
        resource.setLastName(profile.getLastName());

        if (profile.getDocumentId() != null) {
            resource.setDocumentId(profile.getDocumentId().getValue());
        }

        if (profile.getPhoneNumber() != null) {
            resource.setPhoneCountryCode(profile.getPhoneNumber().getCountryCode());
            resource.setPhoneNumber(profile.getPhoneNumber().getNumber());
        }

        if (profile.getAddress() != null) {
            resource.setStreet(profile.getAddress().getStreet());
            resource.setCity(profile.getAddress().getCity());
            resource.setPostalCode(profile.getAddress().getPostalCode());
            resource.setCountry(profile.getAddress().getCountry());
        }

        resource.setMonthlyIncome(profile.getMonthlyIncome());
        resource.setEmploymentStatus(profile.getEmploymentStatus());

        return resource;
    }
}