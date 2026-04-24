package api.turbocredit_backend.loans.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Currency {
    private String code;

    public static Currency PEN() {
        return new Currency("PEN");
    }

    public static Currency USD() {
        return new Currency("USD");
    }

    public static Currency of(String code) {
        if (!code.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Invalid currency code: " + code);
        }
        return new Currency(code);
    }

    public boolean isPEN() {
        return "PEN".equals(code);
    }

    public boolean isUSD() {
        return "USD".equals(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}