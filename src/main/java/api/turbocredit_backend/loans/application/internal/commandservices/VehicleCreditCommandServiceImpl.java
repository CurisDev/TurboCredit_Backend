package api.turbocredit_backend.loans.application.internal.commandservices;

import api.turbocredit_backend.loans.domain.exceptions.LoanNotFoundException;
import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import api.turbocredit_backend.loans.domain.model.entities.VehicleDetails;
import api.turbocredit_backend.loans.domain.services.*;
import api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories.PaymentScheduleItemRepository;
import api.turbocredit_backend.loans.infrastructure.persistence.jpa.repositories.VehicleCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleCreditCommandServiceImpl implements VehicleCreditCommandService {

    private final VehicleCreditRepository vehicleCreditRepository;
    private final PaymentScheduleItemRepository paymentScheduleItemRepository;
    private final AmortizationCalculationService amortizationCalculationService;
    private final FinancialIndicatorsService financialIndicatorsService;

    @Override
    public VehicleCredit createVehicleCredit(VehicleCredit credit) {
        // Calcula la cuota fija
        credit.calculateFixedInstallment();

        // Calcula interés total
        credit.calculateTotalInterest();

        // Calcula total a pagar
        credit.calculateTotalPaid();

        // Guarda el crédito
        VehicleCredit savedCredit = vehicleCreditRepository.save(credit);

        // Genera cronograma de pagos
        List<PaymentScheduleItem> schedule = amortizationCalculationService
                .generatePaymentSchedule(savedCredit);

        // Guarda items del cronograma
        paymentScheduleItemRepository.saveAll(schedule);

        // Calcula indicadores financieros
        savedCredit.setNpv(financialIndicatorsService.calculateNPV(savedCredit, schedule));
        savedCredit.setIrr(financialIndicatorsService.calculateIRR(savedCredit, schedule));
        savedCredit.setTcea(financialIndicatorsService.calculateTCEA(savedCredit.getPeriodicRate()));

        return vehicleCreditRepository.save(savedCredit);
    }

    @Override
    public VehicleCredit approveLoan(Long creditId) {
        VehicleCredit credit = vehicleCreditRepository.findById(creditId)
                .orElseThrow(() -> new LoanNotFoundException(creditId));
        credit.approve();
        return vehicleCreditRepository.save(credit);
    }

    @Override
    public VehicleCredit activateLoan(Long creditId) {
        VehicleCredit credit = vehicleCreditRepository.findById(creditId)
                .orElseThrow(() -> new LoanNotFoundException(creditId));
        credit.activate();
        return vehicleCreditRepository.save(credit);
    }

    @Override
    public VehicleCredit completeLoan(Long creditId) {
        VehicleCredit credit = vehicleCreditRepository.findById(creditId)
                .orElseThrow(() -> new LoanNotFoundException(creditId));
        credit.complete();
        return vehicleCreditRepository.save(credit);
    }

    @Override
    public void createVehicleDetails(Long creditId, VehicleDetails details) {
        // Este método se completará cuando tengas el repositorio de VehicleDetails
        // vehicleDetailsRepository.save(details);
    }
}