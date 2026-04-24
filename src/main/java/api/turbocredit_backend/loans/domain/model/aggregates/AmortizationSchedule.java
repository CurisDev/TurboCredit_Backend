package api.turbocredit_backend.loans.domain.model.aggregates;

import api.turbocredit_backend.loans.domain.model.entities.PaymentScheduleItem;
import api.turbocredit_backend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Entity
@Table(name = "amortization_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmortizationSchedule extends AuditableAbstractAggregateRoot<AmortizationSchedule> {

    @Column(name = "credit_id", nullable = false, unique = true)
    private Long creditId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_periods", nullable = false)
    private Integer totalPeriods;

    @Column(name = "total_interest", nullable = false)
    private BigDecimal totalInterest;

    @Transient
    private List<PaymentScheduleItem> scheduleItems;

    public AmortizationSchedule(Long creditId, UUID userId, Integer totalPeriods) {
        this.creditId = creditId;
        this.userId = userId;
        this.totalPeriods = totalPeriods;
        this.scheduleItems = new ArrayList<>();
    }

    public void addScheduleItem(PaymentScheduleItem item) {
        if (scheduleItems == null) {
            scheduleItems = new ArrayList<>();
        }
        scheduleItems.add(item);
    }

    public BigDecimal getTotalAmortization() {
        if (scheduleItems == null || scheduleItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return scheduleItems.stream()
                .map(PaymentScheduleItem::getAmortization)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPayments() {
        if (scheduleItems == null || scheduleItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return scheduleItems.stream()
                .map(PaymentScheduleItem::getInstallment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}