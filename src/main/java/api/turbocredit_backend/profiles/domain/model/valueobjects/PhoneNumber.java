package api.turbocredit_backend.profiles.domain.model.valueobjects;

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
public class PhoneNumber {
    private String countryCode;
    private String number;

    public static PhoneNumber of(String countryCode, String number) {
        return new PhoneNumber(countryCode, number);
    }

    public String getFullNumber() {
        return countryCode + number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }

    @Override
    public String toString() {
        return countryCode + number;
    }
}