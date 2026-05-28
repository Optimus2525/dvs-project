package lv.smiltenesnkup.dvs.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.smiltenesnkup.dvs.task.enums.TaskPriority;
import lv.smiltenesnkup.dvs.task.enums.TaskType;

import java.time.LocalDate;
import java.util.List;

/**
 * Pārnes galvenā uzdevuma datus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;

    @NotNull(message = "Uzdevuma tips ir obligāts")
    private TaskType taskType;

    @NotBlank(message = "Virsraksts ir obligāts")
    private String title;

    @NotNull(message = "Sākuma datums ir obligāts")
    private LocalDate startDate;

    @NotNull(message = "Izpildes datums ir obligāts")
    private LocalDate dueDate;

    @NotBlank(message = "Atbildīgā persona ir obligāta")
    private String assignee;

    @NotBlank(message = "Izveidotājs ir obligāts")
    private String createdBy;

    private List<String> followers;

    @NotBlank(message = "Apraksts ir obligāts")
    private String description;

    @NotNull(message = "Prioritāte ir obligāta")
    private TaskPriority priority;

    @NotBlank(message = "Statuss ir obligāts")
    private String status;

    private Long documentCardId;

    @Valid
    @Size(max = 3, message = "Vienam uzdevumam nevar būt vairāk par 3 apakšuzdevumiem")
    private List<SubTaskDTO> subTasks;
}