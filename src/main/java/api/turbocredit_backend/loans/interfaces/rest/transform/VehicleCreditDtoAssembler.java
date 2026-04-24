package api.turbocredit_backend.loans.interfaces.rest.transform;

import api.turbocredit_backend.loans.domain.model.aggregates.VehicleCredit;
import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import api.turbocredit_backend.loans.interfaces.rest.resources.AmortizationScheduleResource;
import api.turbocredit_backend.loans.interfaces.rest.resources.PaymentScheduleItemResource;
import api.turbocredit_backend.loans.interfaces.rest.resources.VehicleCreditResource;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleCreditDtoAssembler {

    public static VehicleCreditResource toResource(VehicleCredit credit) {
        VehicleCreditResource resource = new VehicleCreditResource();
        resource.setId(credit.getId());
        resource.setUserId(credit.getUserId());
        resource.setVehiclePrice(credit.getVehiclePrice());
        resource.setDownPayment(credit.getDownPayment());
        resource.setLoanAmount(credit.getLoanAmount());
        resource.setInterestRate(credit.getInterestRate().getValue());
        resource.setRateType(credit.getInterestRate().getType());
        resource.setTermMonths(credit.getTermMonths());
        resource.setGracePeriodMonths(credit.getGracePeriodMonths());
        resource.setGracePeriodType(credit.getGracePeriodType().toString());
        resource.setCurrency(credit.getCurrency().getCode());
        resource.setInsuranceCost(credit.getInsuranceCost());
        resource.setPrincipalFinanced(credit.getPrincipalFinanced());
        resource.setPeriodicRate(credit.getPeriodicRate());
        resource.setFixedInstallment(credit.getFixedInstallment());
        resource.setTotalInterestPaid(credit.getTotalInterestPaid());
        resource.setTotalPaid(credit.getTotalPaid());
        resource.setNpv(credit.getNpv());
        resource.setIrr(credit.getIrr());
        resource.setTcea(credit.getTcea());
        resource.setStatus(credit.getStatus().toString());
        resource.setBankName(credit.getBankName());
        return resource;
    }

    public static AmortizationScheduleResource toAmortizationResource(
            VehicleCredit credit,
            List<PaymentScheduleItem> scheduleItems) {

        AmortizationScheduleResource resource = new AmortizationScheduleResource();
        resource.setCreditId(credit.getId());
        resource.setTotalPeriods(credit.getTermMonths());
        resource.setTotalInterest(credit.getTotalInterestPaid());
        resource.setTotalPrincipal(credit.getPrincipalFinanced());
        resource.setTotalPayments(credit.getTotalPaid());

        List<PaymentScheduleItemResource> itemResources = scheduleItems.stream()
                .map(VehicleCreditDtoAssembler::toPaymentScheduleItemResource)
                .collect(Collectors.toList());

        resource.setScheduleItems(itemResources);
        return resource;
    }

    public static PaymentScheduleItemResource toPaymentScheduleItemResource(PaymentScheduleItem item) {
        PaymentScheduleItemResource resource = new PaymentScheduleItemResource();
        resource.setId(item.getId());
        resource.setCreditId(item.getCreditId());
        resource.setPeriod(item.getPeriod());
        resource.setInstallment(item.getInstallment());
        resource.setInterest(item.getInterest());
        resource.setAmortization(item.getAmortization());
        resource.setRemainingBalance(item.getRemainingBalance());
        resource.setIsGracePeriod(item.getIsGracePeriod());
        resource.setIsPaid(item.getIsPaid());
        resource.setDueDate(item.getDueDate());
        resource.setPaidAt(item.getPaidAt());
        return resource;
    }
}