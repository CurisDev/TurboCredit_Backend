package api.turbocredit_backend.profiles.domain.model.aggregates;

import api.turbocredit_backend.profiles.domain.model.valueobjects.Address;
import api.turbocredit_backend.profiles.domain.model.valueobjects.DocumentId;
import api.turbocredit_backend.profiles.domain.model.valueobjects.PhoneNumber;
import api.turbocredit_backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "document_id", nullable = false, unique = true))
    })
    private DocumentId documentId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "countryCode", column = @Column(name = "phone_country_code")),
            @AttributeOverride(name = "number", column = @Column(name = "phone_number"))
    })
    private PhoneNumber phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country"))
    })
    private Address address;

    @Column(name = "monthly_income")
    private Double monthlyIncome;

    @Column(name = "employment_status")
    private String employmentStatus;

    public Profile(UUID userId, String firstName, String lastName, DocumentId documentId) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentId = documentId;
    }

    public void updatePersonalInfo(String firstName, String lastName, PhoneNumber phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void updateFinancialInfo(Double monthlyIncome, String employmentStatus) {
        this.monthlyIncome = monthlyIncome;
        this.employmentStatus = employmentStatus;
    }
}