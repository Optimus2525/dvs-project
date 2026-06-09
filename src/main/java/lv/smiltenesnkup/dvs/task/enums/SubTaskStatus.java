package lv.smiltenesnkup.dvs.task.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Definē apakšuzdevuma statusus.
 */
public enum SubTaskStatus {
    NOT_STARTED("Nav sākts"),
    WAITING("Gaida uz citu"),
    IN_PROGRESS("Notiek izpilde"),
    RETURNED("Atgriezts labošanai"),
    COMPLETED("Pabeigts");

    private final String label;

    SubTaskStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static SubTaskStatus fromLabel(String label) {
        for (SubTaskStatus status : values()) {
            if (status.label.equals(label)) return status;
        }
        throw new IllegalArgumentException("Nezināms statuss: " + label);
    }

}