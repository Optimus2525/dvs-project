package lv.smiltenesnkup.dvs.task.repository;

import lv.smiltenesnkup.dvs.task.model.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Nodrošina datu bāzes operācijas apakšuzdevumiem.
 */
@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

    /**
     * Atrod konkrētu apakšuzdevumu pēc tā piederības galvenajam uzdevumam un kārtas numura.
     */
    SubTask findByParentTaskIdAndOrderIndex(Long parentTaskId, Integer orderIndex);

}