package api.turbocredit_backend.loans.domain.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "payment_schedule_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "credit_id", nullable = false)
    private Long creditId;  // ✅ IMPORTANTE: Es Long, no UUID

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(nullable = false)
    private Integer period;

    @NotNull
    @Column(nullable = false)
    private BigDecimal installment;

    @NotNull
    @Column(nullable = false)
    private BigDecimal interest;

    @NotNull
    @Column(nullable = false)
    private BigDecimal amortization;

    @Column(name = "life_insurance", precision = 15, scale = 2)
    private BigDecimal lifeInsurance = BigDecimal.ZERO;

    @Column(name = "vehicular_insurance", precision = 15, scale = 2)
    private BigDecimal vehicularInsurance = BigDecimal.ZERO;

    @Column(name = "portes", precision = 15, scale = 2)
    private BigDecimal portes = BigDecimal.ZERO;


    @Column(name = "total_installment", precision = 15, scale = 2)
    private BigDecimal totalInstallment = BigDecimal.ZERO;

    @NotNull
    @Column(name = "remaining_balance", nullable = false)
    private BigDecimal remainingBalance;

    @Column(name = "is_grace_period", nullable = false)
    private Boolean isGracePeriod = false;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    @Column(name = "paid_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidAt;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    public void markAsPaid() {
        this.isPaid = true;
        this.paidAt = new Date();
    }

    public boolean isOverdue() {
        return !isPaid && dueDate != null && new Date().after(dueDate);
    }
}