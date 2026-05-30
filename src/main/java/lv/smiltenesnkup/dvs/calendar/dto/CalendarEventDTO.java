package lv.smiltenesnkup.dvs.calendar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Pārnes kalendāra notikuma pilnos datus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {

    private Long id;

    private String sharepointItemId;

    @NotBlank(message = "Notikuma virsraksts ir obligāts")
    private String title;

    private String description;

    @NotNull(message = "Sākuma laiks ir obligāts")
    private LocalDateTime startTime;

    @NotNull(message = "Beigu laiks ir obligāts")
    private LocalDateTime endTime;

    private boolean allDay;

    @NotNull(message = "Kategorija ir obligāta")
    private Long categoryId;

    private String categoryName;

    private String categoryColor;
}