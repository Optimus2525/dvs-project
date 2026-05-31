package lv.smiltenesnkup.dvs.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.task.dto.NotificationDTO;
import lv.smiltenesnkup.dvs.task.dto.SubTaskDTO;
import lv.smiltenesnkup.dvs.task.dto.TaskDTO;
import lv.smiltenesnkup.dvs.task.dto.TaskUpdateDTO;
import lv.smiltenesnkup.dvs.task.enums.TaskType;
import lv.smiltenesnkup.dvs.task.mapper.NotificationMapper;
import lv.smiltenesnkup.dvs.task.mapper.TaskMapper;
import lv.smiltenesnkup.dvs.task.model.Notification;
import lv.smiltenesnkup.dvs.task.model.SubTask;
import lv.smiltenesnkup.dvs.task.model.Task;
import lv.smiltenesnkup.dvs.task.repository.NotificationRepository;
import lv.smiltenesnkup.dvs.task.repository.SubTaskRepository;
import lv.smiltenesnkup.dvs.task.repository.TaskRepository;
import lv.smiltenesnkup.dvs.common.exception.BusinessLogicException;
import lv.smiltenesnkup.dvs.common.exception.ResourceNotFoundException;
import lv.smiltenesnkup.dvs.task.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementē uzdevumu biznesa loģiku, iekļaujot kompleksos uzdevumus un paziņojumus.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final NotificationRepository notificationRepository;
    private final TaskMapper taskMapper;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        log.info("Tiek meklēts uzdevums ar ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Uzdevums nav atrasts ar ID: " + id));
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, TaskUpdateDTO updateDTO) {
        log.info("Tiek atjaunināti dati uzdevumam ar ID: {}", id);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Uzdevums nav atrasts ar ID: " + id));


        // Tiek atjaunināti tikai pamatdati (virknes un datumi)
        existingTask.setTitle(updateDTO.getTitle());
        existingTask.setDescription(updateDTO.getDescription());
        existingTask.setStartDate(updateDTO.getStartDate());
        existingTask.setDueDate(updateDTO.getDueDate());

        Task savedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(savedTask);
    }


    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        log.info("Tiek izveidots jauns uzdevums: {}", taskDTO.getTitle());

        // Validē datumus
        if (taskDTO.getDueDate().isBefore(taskDTO.getStartDate())) {
            throw new BusinessLogicException("Izpildes datums nevar būt mazāks par sākuma datumu.");
        }

        // Pārbauda maksimālo apakšuzdevumu skaitu
        if (taskDTO.getSubTasks() != null && taskDTO.getSubTasks().size() > 3) {
            throw new BusinessLogicException("Vienam uzdevumam nevar būt vairāk par 3 apakšuzdevumiem.");
        }

        // Apstrādā Komplekso uzdevumu loģiku
        if (taskDTO.getTaskType() == TaskType.COMPLEX_SEQUENTIAL || taskDTO.getTaskType() == TaskType.COMPLEX_PARALLEL) {
            if (taskDTO.getSubTasks() == null || taskDTO.getSubTasks().size() < 2) {
                throw new BusinessLogicException("Kompleksajam uzdevumam jābūt vismaz 2 apakšuzdevumiem.");
            }

            LocalDate previousDate = taskDTO.getStartDate();
            for (int i = 0; i < taskDTO.getSubTasks().size(); i++) {
                SubTaskDTO st = taskDTO.getSubTasks().get(i);
                st.setOrderIndex(i + 1);

                // Datumu validācija kompleksajiem apakšuzdevumiem
                if (st.getDueDate() == null) {
                    throw new BusinessLogicException("Apakšuzdevumam jānorāda izpildes datums.");
                }
                if (st.getDueDate().isBefore(previousDate)) {
                    throw new BusinessLogicException(st.getOrderIndex() + ". apakšuzdevuma datums nevar būt mazāks par iepriekšējo soli.");
                }
                if (st.getDueDate().isAfter(taskDTO.getDueDate())) {
                    throw new BusinessLogicException("Apakšuzdevuma datums nevar pārsniegt galvenā uzdevuma termiņu.");
                }
                previousDate = st.getDueDate();

                // Iestata sākotnējos statusus atkarībā no tipa
                if (taskDTO.getTaskType() == TaskType.COMPLEX_PARALLEL) {
                    st.setActive(true);
                    st.setStatus("Nav sākts");
                } else { // COMPLEX_SEQUENTIAL
                    if (i == 0) {
                        st.setActive(true);
                        st.setStatus("Nav sākts");
                    } else {
                        st.setActive(false);
                        st.setStatus("Gaida uz citu");
                    }
                }
            }
        } else {
            // Parasto (REGULAR) uzdevumu apstrāde
            if (taskDTO.getSubTasks() != null) {
                for (int i = 0; i < taskDTO.getSubTasks().size(); i++) {
                    SubTaskDTO st = taskDTO.getSubTasks().get(i);
                    // Tiek iestatīti obligātie noklusējuma lauki, lai nerastos datubāzes kļūdas
                    st.setOrderIndex(i + 1);
                    st.setActive(true);
                    st.setStatus("Nav sākts");
                }
            }
        }

        Task taskEntity = taskMapper.toEntity(taskDTO);

        // DROŠĪBAS LABOJUMS: Manuāli iestata taskType un createdBy
        if (taskDTO.getTaskType() != null) {
            taskEntity.setTaskType(taskDTO.getTaskType());
        } else {
            taskEntity.setTaskType(TaskType.REGULAR);
        }

        if (taskDTO.getCreatedBy() != null) {
            taskEntity.setCreatedBy(taskDTO.getCreatedBy());
        }

        // Piesaista apakšuzdevumiem galveno uzdevumu, lai Hibernate varētu izveidot relāciju
        if (taskEntity.getSubTasks() != null) {
            taskEntity.getSubTasks().forEach(subTask -> subTask.setParentTask(taskEntity));
        }

        Task savedTask = taskRepository.save(taskEntity);
        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional
    public void updateSubTask(Long subTaskId, String newStatus, String description) {
        log.info("Tiek atjaunināti dati apakšuzdevumam ID: {}", subTaskId);

        SubTask currentSubTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("Apakšuzdevums nav atrasts ar ID: " + subTaskId));

        Task parentTask = currentSubTask.getParentTask();

        // Tiek atjaunināts apraksts, ja tas ir norādīts
        if (description != null) {
            currentSubTask.setDescription(description);
        }

        // Tiek pārbaudīts, vai statuss ir mainījies, lai velti neizsauktu darbplūsmas loģiku
        if (newStatus != null && !newStatus.equals(currentSubTask.getStatus())) {
            currentSubTask.setStatus(newStatus);

            // Darbplūsmas (Workflow) loģika secīgajam uzdevumam
            if (parentTask.getTaskType() == TaskType.COMPLEX_SEQUENTIAL) {

                // 1. GADĪJUMS: Pabeigts -> Aktivizē nākamo
                if ("Pabeigts".equals(newStatus)) {
                    currentSubTask.setActive(false); // Šis lietotājs savu darbu beidza

                    SubTask nextSubTask = subTaskRepository.findByParentTaskIdAndOrderIndex(parentTask.getId(), currentSubTask.getOrderIndex() + 1);
                    if (nextSubTask != null) {
                        nextSubTask.setActive(true);
                        nextSubTask.setStatus("Nav sākts");
                        subTaskRepository.save(nextSubTask);

                        createNotification(nextSubTask.getAssignee(), "Ir pienākusi Tava kārta uzdevumā: " + parentTask.getTitle(), parentTask);
                    }
                    createNotification(parentTask.getAssignee(), currentSubTask.getAssignee() + " pabeidza savu daļu uzdevumā: " + parentTask.getTitle(), parentTask);
                }

                // 2. GADĪJUMS: Atgriezts labošanai -> Aktivizē iepriekšējo
                else if ("Atgriezts labošanai".equals(newStatus)) {
                    currentSubTask.setActive(false);
                    currentSubTask.setStatus("Gaida uz citu");

                    SubTask prevSubTask = subTaskRepository.findByParentTaskIdAndOrderIndex(parentTask.getId(), currentSubTask.getOrderIndex() - 1);
                    if (prevSubTask != null) {
                        prevSubTask.setActive(true);
                        prevSubTask.setStatus("Notiek izpilde"); // Statuss tiek nomainīts uz 'Notiek izpilde'
                        subTaskRepository.save(prevSubTask);

                        createNotification(prevSubTask.getAssignee(), "Tavs darbs tika atgriezts labošanai uzdevumā: " + parentTask.getTitle(), parentTask);
                    }
                    createNotification(parentTask.getAssignee(), "Uzdevums atgriezts labošanai: " + parentTask.getTitle(), parentTask);
                }
            }
        }

        subTaskRepository.save(currentSubTask);
    }

    /**
     * Izveido un saglabā sistēmas paziņojumu.
     */
    private void createNotification(String recipient, String message, Task relatedTask) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .relatedTask(relatedTask)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(String user) {
        return notificationRepository.findAllByRecipientAndReadFalseOrderByCreatedAtDesc(user).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // --- Esošās metodes datu lasīšanai ---

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForAssignee(String assignee) {
        return taskRepository.findAllTasksForUser(assignee).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForDocument(Long documentCardId) {
        return taskRepository.findAllByDocumentCardId(documentCardId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForFollower(String follower) {
        return taskRepository.findAllByFollower(follower).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

}