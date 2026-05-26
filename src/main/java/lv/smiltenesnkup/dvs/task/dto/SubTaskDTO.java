package lv.smiltenesnkup.dvs.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Pārnes apakšuzdevuma datus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskDTO {

    private Long id;

    @NotBlank(message = "Apakšuzdevuma virsraksts ir obligāts")
    private String title;

    private String description;

    private String assignee;

    private LocalDate dueDate;

    private String status;

    private Integer orderIndex;

    // NOMAINĪTS NO boolean UZ Boolean, LAI NOVĒRSTU JSON PARSĒŠANAS KĻŪDU
    private Boolean active;
}