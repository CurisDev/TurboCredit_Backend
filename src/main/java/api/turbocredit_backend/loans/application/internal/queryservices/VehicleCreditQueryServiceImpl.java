package api.turbocredit_backend.loans.application.internal.queryservices;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import api.turbocredit_backend.loans.domain.services.VehicleCreditQueryService;
import api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories.PaymentScheduleItemRepository;
import api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories.VehicleCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VehicleCreditQueryServiceImpl implements VehicleCreditQueryService {

    private final VehicleCreditRepository vehicleCreditRepository;
    private final PaymentScheduleItemRepository paymentScheduleItemRepository;

    @Override
    public Optional<VehicleCredit> findById(Long id) {
        return vehicleCreditRepository.findById(id);
    }

    @Override
    public List<VehicleCredit> findByUserId(UUID userId) {
        return vehicleCreditRepository.findByUserId(userId);
    }

    @Override
    public List<PaymentScheduleItem> getPaymentSchedule(Long creditId) {
        return paymentScheduleItemRepository.findByCreditIdOrderByPeriod(creditId);
    }
}