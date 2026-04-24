package api.turbocredit_backend.loans.domain.model.valueobjects;

public enum GracePeriodType {
    TOTAL("Total - Sin interés"),
    PARTIAL("Parcial - Solo interés");

    private final String description;

    GracePeriodType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTotal() {
        return this == TOTAL;
    }

    public boolean isPartial() {
        return this == PARTIAL;
    }
}