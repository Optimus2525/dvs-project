package lv.smiltenesnkup.dvs.calendar.repository;

import lv.smiltenesnkup.dvs.calendar.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Nodrošina datu bāzes operācijas kalendāra notikumiem.
 */
@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /**
     * Atrod notikumus norādītajā laika periodā (piemēram, vienam mēnesim).
     * Iekļauj notikumus, kas sākas, beidzas vai pārklājas ar norādīto periodu.
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime <= :end AND e.endTime >= :start ORDER BY e.startTime ASC")
    List<CalendarEvent> findEventsInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Atrod lokālo notikumu pēc tā SharePoint Graph API identifikatora.
     */
    Optional<CalendarEvent> findBySharepointItemId(String sharepointItemId);


}