package api.turbocredit_backend.loans.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmortizationScheduleResource {

    @Schema(description = "Credit ID", example = "1")
    private Long creditId;

    @Schema(description = "Total number of periods", example = "60")
    private Integer totalPeriods;

    @Schema(description = "Total interest to be paid", example = "10190.00")
    private BigDecimal totalInterest;

    @Schema(description = "Total principal to be paid", example = "40000.00")
    private BigDecimal totalPrincipal;

    @Schema(description = "Total amount to be paid", example = "50190.00")
    private BigDecimal totalPayments;

    @Schema(description = "Payment schedule details")
    private List<PaymentScheduleItemResource> scheduleItems;
}