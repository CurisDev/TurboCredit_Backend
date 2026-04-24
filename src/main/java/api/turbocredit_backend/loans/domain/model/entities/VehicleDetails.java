package api.turbocredit_backend.loans.domain.model.entities;

import api.turbocredit_backend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetails extends AuditableModel {

    @NotNull
    @Column(name = "credit_id", nullable = false, unique = true)
    private Long creditId;

    @NotBlank
    @Column(nullable = false)
    private String brand;

    @NotBlank
    @Column(nullable = false)
    private String model;

    @NotNull
    @Column(nullable = false)
    private Integer year;

    @Column(name = "vin", unique = true)
    private String vin;

    @Column(name = "license_plate")
    private String licensePlate;

    public VehicleDetails(Long creditId, String brand, String model, Integer year) {
        this.creditId = creditId;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public String getVehicleDescription() {
        return String.format("%s %s %d", brand, model, year);
    }
}