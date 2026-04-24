package api.turbocredit_backend.loans.domain.services;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleCreditQueryService {
    Optional<VehicleCredit> findById(Long id);
    List<VehicleCredit> findByUserId(UUID userId);
    List<PaymentScheduleItem> getPaymentSchedule(Long creditId);
}