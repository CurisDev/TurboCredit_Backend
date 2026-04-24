package api.turbocredit_backend.loans.domain.model.valueobjects;

public enum LoanStatus {
    PENDING("Pendiente"),
    APPROVED("Aprobado"),
    ACTIVE("Activo"),
    COMPLETED("Completado"),
    DEFAULTED("Incumplimiento"),
    CANCELLED("Cancelado");

    private final String description;

    LoanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isPending() {
        return this == PENDING;
    }
}