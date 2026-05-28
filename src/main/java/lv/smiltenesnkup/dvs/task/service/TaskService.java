package lv.smiltenesnkup.dvs.task.service;

import lv.smiltenesnkup.dvs.task.dto.TaskDTO;
import lv.smiltenesnkup.dvs.task.dto.TaskUpdateDTO;

import java.util.List;

/**
 * Definē biznesa loģiku darbam ar uzdevumiem.
 */
public interface TaskService {

    TaskDTO createTask(TaskDTO taskDTO);

    /**
     * Izgūst konkrēta uzdevuma datus.
     */
    TaskDTO getTaskById(Long id);

    /**
     * Atjaunina galvenā uzdevuma pamatdatus, izmantojot atjaunināšanas DTO.
     */
    TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO);

    List<TaskDTO> getTasksForAssignee(String assignee);

    List<TaskDTO> getTasksForDocument(Long documentCardId);

    /**
     * Izgūst visus uzdevumus, kuros norādītā persona ir atzīmēta kā sekotājs.
     */
    List<TaskDTO> getTasksForFollower(String follower);

    /**
     * Atjaunina apakšuzdevuma datus (statusu un aprakstu) un apstrādā darbplūsmas (Workflow) loģiku.
     */
    void updateSubTask(Long subTaskId, String newStatus, String description);

    /**
     * Izgūst neizlasītos paziņojumus lietotājam.
     */
    List<lv.smiltenesnkup.dvs.task.dto.NotificationDTO> getUnreadNotifications(String user);

    /**
     * Atzīmē paziņojumu kā izlasītu.
     */
    void markNotificationAsRead(Long notificationId);

}