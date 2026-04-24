package api.turbocredit_backend.loans.domain.model.valueobjects;

import api.turbocredit_backend.loans.domain.exceptions.InvalidInterestRateException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InterestRate {
    private BigDecimal value;
    private String type; // EFFECTIVE or NOMINAL

    public static InterestRate effectiveAnnual(BigDecimal tea) {
        if (tea == null || tea.compareTo(BigDecimal.ZERO) < 0 || tea.compareTo(new BigDecimal(100)) > 0) {
            throw new InvalidInterestRateException(tea);
        }
        return new InterestRate(tea, "EFFECTIVE");
    }

    public static InterestRate nominal(BigDecimal tna) {
        if (tna == null || tna.compareTo(BigDecimal.ZERO) < 0 || tna.compareTo(new BigDecimal(100)) > 0) {
            throw new InvalidInterestRateException(tna);
        }
        return new InterestRate(tna, "NOMINAL");
    }

    /**
     * Convierte TEA a tasa mensual (efectiva)
     * Fórmula: tm = (1 + TEA)^(1/12) - 1
     */
    public BigDecimal toMonthlyEffectiveRate() {
        if (isEffective()) {
            BigDecimal tea = value.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
            BigDecimal onePlusTea = BigDecimal.ONE.add(tea);
            double monthlyRate = Math.pow(onePlusTea.doubleValue(), 1.0 / 12.0) - 1;
            return new BigDecimal(monthlyRate)
                    .multiply(new BigDecimal(100))
                    .setScale(10, RoundingMode.HALF_UP);
        } else {
            // TNA: convertir a tasa mensual nominal = TNA / 12
            return value.divide(new BigDecimal(12), 10, RoundingMode.HALF_UP);
        }
    }

    public boolean isEffective() {
        return "EFFECTIVE".equals(type);
    }

    public boolean isNominal() {
        return "NOMINAL".equals(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestRate that = (InterestRate) o;
        return Objects.equals(value, that.value) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public String toString() {
        return value + "% (" + type + ")";
    }
}