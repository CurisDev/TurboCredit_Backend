package api.turbocredit_backend.loans.domain.model.valueobjects;

public enum RateType {
    EFFECTIVE("Efectiva"),
    NOMINAL("Nominal");

    private final String description;

    RateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEffective() {
        return this == EFFECTIVE;
    }

    public boolean isNominal() {
        return this == NOMINAL;
    }
}