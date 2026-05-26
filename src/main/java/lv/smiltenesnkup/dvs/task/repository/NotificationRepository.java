package lv.smiltenesnkup.dvs.task.repository;

import lv.smiltenesnkup.dvs.task.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Nodrošina datu bāzes operācijas paziņojumu entītijai.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Atrod visus neizlasītos paziņojumus konkrētam lietotājam.
     */
    List<Notification> findAllByRecipientAndReadFalseOrderByCreatedAtDesc(String recipient);

}