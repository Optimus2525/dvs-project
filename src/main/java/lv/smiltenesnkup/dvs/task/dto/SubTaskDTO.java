package lv.smiltenesnkup.dvs.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private boolean completed;

}