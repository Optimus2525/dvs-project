package lv.smiltenesnkup.dvs.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Pārnes datus daļējai kalendāra notikuma atjaunināšanai (Partial Update),
 * piemēram, kad notikums tiek pārvilkts (drag & drop) uz citu dienu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventUpdateDTO {

    private String title;
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allDay;
    private Long categoryId;


}