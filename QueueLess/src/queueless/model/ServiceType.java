package queueless.model;

public enum ServiceType {
    CANTEEN("Canteen"),
    ADMIN_OFFICE("Admin Office"),
    MEDICAL_DESK("Medical Desk"),
    HELP_DESK("Help Desk");

    private final String label;

    ServiceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
