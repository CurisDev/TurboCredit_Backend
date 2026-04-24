package api.turbocredit_backend.loans.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleItemResource {

    @Schema(description = "Schedule item ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Credit ID", example = "1")
    private Long creditId;

    @Schema(description = "Payment period number", example = "1")
    private Integer period;

    @Schema(description = "Total installment amount", example = "836.50")
    private BigDecimal installment;

    @Schema(description = "Interest portion", example = "300.00")
    private BigDecimal interest;

    @Schema(description = "Principal (amortization) portion", example = "536.50")
    private BigDecimal amortization;

    @Schema(description = "Remaining balance after payment", example = "39463.50")
    private BigDecimal remainingBalance;

    @Schema(description = "Is grace period payment", example = "false")
    private Boolean isGracePeriod;

    @Schema(description = "Is payment completed", example = "false")
    private Boolean isPaid;

    @Schema(description = "Payment due date", example = "2025-05-23T00:00:00Z")
    private Date dueDate;

    @Schema(description = "Payment date (if paid)", example = "null")
    private Date paidAt;
}