package api.turbocredit_backend.loans.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleCreditRequest {

    @NotNull(message = "Vehicle price is required")
    @DecimalMin(value = "0.01", message = "Vehicle price must be greater than 0")
    @Schema(description = "Total vehicle price", example = "50000.00")
    private BigDecimal vehiclePrice;

    @NotNull(message = "Down payment is required")
    @DecimalMin(value = "0.00", message = "Down payment cannot be negative")
    @Schema(description = "Down payment amount", example = "10000.00")
    private BigDecimal downPayment;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    @DecimalMax(value = "100.00", message = "Interest rate cannot exceed 100%")
    @Schema(description = "Annual interest rate (TEA/TNA)", example = "8.5")
    private BigDecimal interestRate;

    @NotBlank(message = "Rate type is required")
    @Pattern(regexp = "EFFECTIVE|NOMINAL", message = "Rate type must be EFFECTIVE or NOMINAL")
    @Schema(description = "Interest rate type", example = "EFFECTIVE")
    private String rateType;

    @NotNull(message = "Term months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    @Max(value = 360, message = "Term cannot exceed 360 months")
    @Schema(description = "Loan term in months", example = "60")
    private Integer termMonths;

    @NotNull(message = "Grace period months is required")
    @Min(value = 0, message = "Grace period cannot be negative")
    @Schema(description = "Grace period in months", example = "0")
    private Integer gracePeriodMonths;

    @NotBlank(message = "Grace period type is required")
    @Pattern(regexp = "TOTAL|PARTIAL", message = "Grace period type must be TOTAL or PARTIAL")
    @Schema(description = "Grace period type", example = "PARTIAL")
    private String gracePeriodType;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "PEN|USD", message = "Currency must be PEN or USD")
    @Schema(description = "Currency code", example = "PEN")
    private String currency;

    @NotNull(message = "Insurance cost is required")
    @DecimalMin(value = "0.00", message = "Insurance cost cannot be negative")
    @Schema(description = "Insurance cost", example = "500.00")
    private BigDecimal insuranceCost;

    @Schema(description = "Bank name", example = "BCP")
    private String bankName;

    // Para validación de riesgo
    @Schema(description = "Monthly income (for risk validation)", example = "5000.00")
    private BigDecimal monthlyIncome;
}