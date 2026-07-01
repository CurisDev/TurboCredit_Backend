package api.turbocredit_backend.loans.domain.services;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.VehicleDetails;

import java.util.Optional;

public interface VehicleCreditCommandService {
    VehicleCredit createVehicleCredit(VehicleCredit credit);
    void createVehicleDetails(Long creditId, VehicleDetails details);
}