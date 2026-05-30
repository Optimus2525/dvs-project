package lv.smiltenesnkup.dvs.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pārnes kalendāra kategorijas datus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarCategoryDTO {

    private Long id;

    @NotBlank(message = "Kategorijas nosaukums ir obligāts")
    private String name;

    @NotBlank(message = "Krāsas kods ir obligāts")
    private String colorCode;

    private boolean active;

}