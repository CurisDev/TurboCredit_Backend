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

    @Schema(description = "Client name", example = "Juan Perez")
    private String clientName;

    @Schema(description = "Vehicle brand", example = "Toyota")
    private String vehicleBrand;

    @Schema(description = "Vehicle model", example = "Corolla")
    private String vehicleModel;

    @Schema(description = "Residual percentage for final payment", example = "40.0")
    private BigDecimal residualPercentage;

    @Schema(description = "Seguro desgravamen rate", example = "0.0005")
    private BigDecimal seguroDesgravamenRate;

    @Schema(description = "Seguro vehicular monthly cost", example = "150.0")
    private BigDecimal seguroVehicularMonthly;

    @Schema(description = "Portes monthly cost", example = "15.0")
    private BigDecimal portes;

    @Schema(description = "Gastos administrativos monthly cost", example = "30.0")
    private BigDecimal gastosAdministrativos;

    @Schema(description = "Comision desembolso initial cost", example = "500.0")
    private BigDecimal comisionDesembolso;

    @Schema(description = "Comision evaluacion initial cost", example = "265.0")
    private BigDecimal comisionEvaluacion;

    @Schema(description = "Cost of opportunity COK", example = "10.0")
    private BigDecimal cok;

    // --- Resultados calculados en el frontend (fuente de verdad) ---
    // Si se envían, el backend los persiste tal cual en lugar de recalcularlos,
    // garantizando que el historial coincida exactamente con lo mostrado al usuario.

    @Schema(description = "Fixed monthly installment computed on the client", example = "1558.20")
    private BigDecimal fixedInstallment;

    @Schema(description = "Principal financed computed on the client", example = "48000.00")
    private BigDecimal principalFinanced;

    @Schema(description = "Residual value computed on the client", example = "16000.00")
    private BigDecimal residualValue;

    @Schema(description = "Total interest paid computed on the client", example = "8095.20")
    private BigDecimal totalInterestPaid;

    @Schema(description = "Total paid computed on the client", example = "58975.20")
    private BigDecimal totalPaid;

    @Schema(description = "Net Present Value (VAN) computed on the client", example = "850.00")
    private BigDecimal npv;

    @Schema(description = "Internal Rate of Return (TIR) computed on the client", example = "1.52")
    private BigDecimal irr;

    @Schema(description = "Annual Effective Cost Rate (TCEA) computed on the client", example = "19.85")
    private BigDecimal tcea;
}