package lv.smiltenesnkup.dvs.task.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.task.dto.TaskDTO;
import lv.smiltenesnkup.dvs.task.mapper.TaskMapper;
import lv.smiltenesnkup.dvs.task.model.Task;
import lv.smiltenesnkup.dvs.task.repository.TaskRepository;
import lv.smiltenesnkup.dvs.task.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementē uzdevumu biznesa loģiku.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    /**
     * Pārbauda un saglabā jaunu uzdevumu datubāzē.
     */
    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        log.info("Creating new task: {}", taskDTO.getTitle());

        // Pārbauda apakšuzdevumu skaitu (biznesa loģikas līmenī, papildus DTO anotācijai)
        if (taskDTO.getSubTasks() != null && taskDTO.getSubTasks().size() > 3) {
            throw new IllegalArgumentException("Vienam uzdevumam nevar būt vairāk par 3 apakšuzdevumiem.");
        }

        // Pārbauda datumu loģiku
        if (taskDTO.getDueDate().isBefore(taskDTO.getStartDate())) {
            throw new IllegalArgumentException("Izpildes datums nevar būt mazāks par sākuma datumu.");
        }

        Task taskEntity = taskMapper.toEntity(taskDTO);

        // Piesaista apakšuzdevumiem vecākentītiju (Parent Task), lai Hibernate varētu saglabāt relāciju
        if (taskEntity.getSubTasks() != null) {
            taskEntity.getSubTasks().forEach(subTask -> subTask.setParentTask(taskEntity));
        }

        Task savedTask = taskRepository.save(taskEntity);
        return taskMapper.toDto(savedTask);
    }

    /**
     * Izgūst visus uzdevumus, kas piešķirti konkrētai personai.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForAssignee(String assignee) {
        log.info("Fetching tasks for assignee: {}", assignee);
        return taskRepository.findAllByAssigneeOrderByDueDateAsc(assignee).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Izgūst visus uzdevumus, kas saistīti ar konkrētu dokumentu.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForDocument(Long documentCardId) {
        log.info("Fetching tasks for document card ID: {}", documentCardId);
        return taskRepository.findAllByDocumentCardId(documentCardId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Izgūst visus uzdevumus, kuros norādītā persona ir atzīmēta kā sekotājs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksForFollower(String follower) {
        log.info("Fetching tasks where user is follower: {}", follower);
        return taskRepository.findAllByFollower(follower).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

}