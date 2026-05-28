package lv.smiltenesnkup.dvs.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Pārnes atjaunināmos datus galvenajam uzdevumam, izslēdzot laukus, kurus nedrīkst mainīt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDTO {

    @NotBlank(message = "Virsraksts ir obligāts")
    private String title;

    @NotBlank(message = "Apraksts ir obligāts")
    private String description;

    @NotNull(message = "Sākuma datums ir obligāts")
    private LocalDate startDate;

    @NotNull(message = "Izpildes datums ir obligāts")
    private LocalDate dueDate;


}