package lv.smiltenesnkup.dvs.task.service;

import lv.smiltenesnkup.dvs.task.dto.TaskDTO;

import java.util.List;

/**
 * Definē biznesa loģiku darbam ar uzdevumiem.
 */
public interface TaskService {

    TaskDTO createTask(TaskDTO taskDTO);

    List<TaskDTO> getTasksForAssignee(String assignee);

    List<TaskDTO> getTasksForDocument(Long documentCardId);

    /**
     * Izgūst visus uzdevumus, kuros norādītā persona ir atzīmēta kā sekotājs.
     */
    List<TaskDTO> getTasksForFollower(String follower);

    /**
     * Atjaunina apakšuzdevuma statusu un apstrādā darbplūsmas (Workflow) loģiku.
     */
    void updateSubTaskStatus(Long subTaskId, String newStatus, String comment);

    /**
     * Izgūst neizlasītos paziņojumus lietotājam.
     */
    List<lv.smiltenesnkup.dvs.task.dto.NotificationDTO> getUnreadNotifications(String user);

    /**
     * Atzīmē paziņojumu kā izlasītu.
     */
    void markNotificationAsRead(Long notificationId);

}