package api.turbocredit_backend.loans.application.internal.commandservices;

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
        // Solo recalcula cuando el cliente no envió los resultados (fuente de verdad = frontend)
        if (credit.getFixedInstallment() == null) {
            credit.calculateFixedInstallment();
        }
        if (credit.getTotalInterestPaid() == null
                || credit.getTotalInterestPaid().compareTo(java.math.BigDecimal.ZERO) == 0) {
            credit.calculateTotalInterest();
        }
        if (credit.getTotalPaid() == null
                || credit.getTotalPaid().compareTo(java.math.BigDecimal.ZERO) == 0) {
            credit.calculateTotalPaid();
        }

        // Guarda el crédito
        VehicleCredit savedCredit = vehicleCreditRepository.save(credit);

        // Genera y guarda el cronograma de pagos (usando la cuota fija ya definida)
        List<PaymentScheduleItem> schedule = amortizationCalculationService
                .generatePaymentSchedule(savedCredit);
        paymentScheduleItemRepository.saveAll(schedule);

        // Indicadores financieros: se respetan los del cliente si vinieron; si no, se calculan
        if (savedCredit.getNpv() == null) {
            savedCredit.setNpv(financialIndicatorsService.calculateNPV(savedCredit, schedule));
        }
        if (savedCredit.getIrr() == null) {
            savedCredit.setIrr(financialIndicatorsService.calculateIRR(savedCredit, schedule));
        }
        if (savedCredit.getTcea() == null) {
            savedCredit.setTcea(financialIndicatorsService.calculateTCEA(savedCredit.getIrr()));
        }

        return vehicleCreditRepository.save(savedCredit);
    }

    @Override
    public VehicleCredit updateVehicleCredit(Long creditId, VehicleCredit updated, java.util.UUID userId) {
        VehicleCredit existing = vehicleCreditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle credit not found: " + creditId));

        if (!existing.getUserId().equals(userId)) {
            throw new SecurityException("The credit does not belong to the authenticated user");
        }

        // Elimina el cronograma anterior antes de regenerarlo
        List<PaymentScheduleItem> oldItems = paymentScheduleItemRepository
                .findByCreditIdOrderByPeriod(creditId);
        if (!oldItems.isEmpty()) {
            paymentScheduleItemRepository.deleteAll(oldItems);
        }

        // Copia los nuevos valores sobre el registro existente (conserva id y userId)
        existing.setClientName(updated.getClientName());
        existing.setVehicleBrand(updated.getVehicleBrand());
        existing.setVehicleModel(updated.getVehicleModel());
        existing.setVehiclePrice(updated.getVehiclePrice());
        existing.setDownPayment(updated.getDownPayment());
        existing.setLoanAmount(updated.getLoanAmount());
        existing.setInterestRate(updated.getInterestRate());
        existing.setPeriodicRate(updated.getPeriodicRate());
        existing.setTermMonths(updated.getTermMonths());
        existing.setGracePeriodMonths(updated.getGracePeriodMonths());
        existing.setGracePeriodType(updated.getGracePeriodType());
        existing.setCurrency(updated.getCurrency());
        existing.setInsuranceCost(updated.getInsuranceCost());
        existing.setResidualPercentage(updated.getResidualPercentage());
        existing.setResidualValue(updated.getResidualValue());
        existing.setSeguroDesgravamenRate(updated.getSeguroDesgravamenRate());
        existing.setSeguroVehicularMonthly(updated.getSeguroVehicularMonthly());
        existing.setPortes(updated.getPortes());
        existing.setGastosAdministrativos(updated.getGastosAdministrativos());
        existing.setComisionDesembolso(updated.getComisionDesembolso());
        existing.setComisionEvaluacion(updated.getComisionEvaluacion());
        existing.setCok(updated.getCok());
        existing.setBankName(updated.getBankName());
        existing.setPrincipalFinanced(updated.getPrincipalFinanced());
        existing.setFixedInstallment(updated.getFixedInstallment());
        existing.setTotalInterestPaid(updated.getTotalInterestPaid());
        existing.setTotalPaid(updated.getTotalPaid());
        existing.setNpv(updated.getNpv());
        existing.setIrr(updated.getIrr());
        existing.setTcea(updated.getTcea());

        // Recalcula lo que el cliente no haya provisto
        if (existing.getFixedInstallment() == null) {
            existing.calculateFixedInstallment();
        }
        if (existing.getTotalInterestPaid() == null
                || existing.getTotalInterestPaid().compareTo(java.math.BigDecimal.ZERO) == 0) {
            existing.calculateTotalInterest();
        }
        if (existing.getTotalPaid() == null
                || existing.getTotalPaid().compareTo(java.math.BigDecimal.ZERO) == 0) {
            existing.calculateTotalPaid();
        }

        VehicleCredit savedCredit = vehicleCreditRepository.save(existing);

        List<PaymentScheduleItem> schedule = amortizationCalculationService
                .generatePaymentSchedule(savedCredit);
        paymentScheduleItemRepository.saveAll(schedule);

        if (savedCredit.getNpv() == null) {
            savedCredit.setNpv(financialIndicatorsService.calculateNPV(savedCredit, schedule));
        }
        if (savedCredit.getIrr() == null) {
            savedCredit.setIrr(financialIndicatorsService.calculateIRR(savedCredit, schedule));
        }
        if (savedCredit.getTcea() == null) {
            savedCredit.setTcea(financialIndicatorsService.calculateTCEA(savedCredit.getIrr()));
        }

        return vehicleCreditRepository.save(savedCredit);
    }

    @Override
    public void createVehicleDetails(Long creditId, VehicleDetails details) {
        // Este método se completará cuando tengas el repositorio de VehicleDetails
        // vehicleDetailsRepository.save(details);
    }
}