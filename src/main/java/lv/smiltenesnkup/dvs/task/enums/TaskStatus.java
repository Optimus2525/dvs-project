package lv.smiltenesnkup.dvs.task.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Definē galvenā uzdevuma statusus.
 */
public enum TaskStatus {
    NOT_STARTED("Nav sākts"),
    IN_PROGRESS("Notiek izpilde"),
    COMPLETED("Pabeigts");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static TaskStatus fromLabel(String label) {
        for (TaskStatus status : values()) {
            if (status.label.equals(label)) return status;
        }
        throw new IllegalArgumentException("Nezināms statuss: " + label);
    }

}