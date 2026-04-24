package api.turbocredit_backend.profiles.domain.model.valueobjects;

import api.turbocredit_backend.profiles.domain.exceptions.InvalidDocumentIdException;
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
public class DocumentId {
    private String value;

    public static DocumentId of(String value) {
        if (value == null || value.trim().isEmpty() || !value.matches("^[a-zA-Z0-9]{8,12}$")) {
            throw new InvalidDocumentIdException(value);
        }
        return new DocumentId(value.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentId that = (DocumentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}