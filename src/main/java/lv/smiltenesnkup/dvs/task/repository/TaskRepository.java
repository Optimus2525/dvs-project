package lv.smiltenesnkup.dvs.task.repository;

import lv.smiltenesnkup.dvs.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Nodrošina datu bāzes operācijas uzdevumu entītijai.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

//    /**
//     * Atrod visus uzdevumus, kur norādītā persona ir atbildīgā.
//     */
//    List<Task> findAllByAssigneeOrderByDueDateAsc(String assignee);

    /**
     * Atrod visus uzdevumus, kas piesaistīti konkrētai dokumenta kartītei.
     */
    List<Task> findAllByDocumentCardId(Long documentCardId);

    /**
     * Native PostgreSQL vaicājums, kas atrod visus uzdevumus,
     * kuros norādītais lietotājs ir pievienots kā sekotājs (followers JSONB masīvā).
     */
    @Query(value = "SELECT * FROM task WHERE followers @> jsonb_build_array(:follower) ORDER BY due_date ASC", nativeQuery = true)
    List<Task> findAllByFollower(@org.springframework.data.repository.query.Param("follower") String follower);

    /**
     * Atrod visus uzdevumus, kur norādītā persona ir atbildīgā par galveno uzdevumu
     * VAI ir atbildīgā par kādu no šī uzdevuma apakšuzdevumiem.
     * Izmanto DISTINCT, lai novērstu dublikātus.
     */
    @Query(value = "SELECT DISTINCT t.* FROM task t " +
            "LEFT JOIN sub_task st ON t.id = st.parent_task_id " +
            "WHERE t.assignee = :assignee OR st.assignee = :assignee " +
            "ORDER BY t.due_date ASC", nativeQuery = true)
    List<Task> findAllTasksForUser(@org.springframework.data.repository.query.Param("assignee") String assignee);

}