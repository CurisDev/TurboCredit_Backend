package api.turbocredit_backend.loans.domain.services;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.VehicleDetails;

import java.util.Optional;

public interface VehicleCreditCommandService {
    VehicleCredit createVehicleCredit(VehicleCredit credit);
    VehicleCredit approveLoan(Long creditId);
    VehicleCredit activateLoan(Long creditId);
    VehicleCredit completeLoan(Long creditId);
    void createVehicleDetails(Long creditId, VehicleDetails details);
}