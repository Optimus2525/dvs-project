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
     * Atrod notikumus norādītajā laika periodā, kurus izveidojis TIKAI norādītais lietotājs,
     * VAI kuros norādītais lietotājs ir pievienots kā uzaicinātā persona.
     */
    @Query(value = "SELECT * FROM calendar_event WHERE start_time <= :end AND end_time >= :start " +
            "AND (created_by = :user OR invited_persons @> jsonb_build_array(:user)) " +
            "ORDER BY start_time ASC", nativeQuery = true)
    List<CalendarEvent> findEventsInPeriodForUser(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("user") String user);

    /**
     * Pārbauda, vai lietotājam jau nav ieplānots notikums norādītajā laika nogrieznī.
     */
    @Query(value = "SELECT COUNT(*) FROM calendar_event WHERE start_time < :end AND end_time > :start " +
            "AND (created_by = :user OR invited_persons @> jsonb_build_array(:user))", nativeQuery = true)
    long countOverlappingEventsForUser(@Param("user") String user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Atrod lokālo notikumu pēc tā SharePoint Graph API identifikatora.
     */
    Optional<CalendarEvent> findBySharepointItemId(String sharepointItemId);


}