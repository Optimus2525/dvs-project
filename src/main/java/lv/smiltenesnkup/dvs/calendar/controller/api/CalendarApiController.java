package lv.smiltenesnkup.dvs.calendar.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventDTO;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventUpdateDTO;
import lv.smiltenesnkup.dvs.calendar.service.CalendarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST kontrolieris kalendāra notikumu pārvaldībai.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarApiController {

    private final CalendarService calendarService;

    /**
     * Izgūst notikumus norādītajā laika periodā konkrētam lietotājam.
     */
    @GetMapping("/events")
    public ResponseEntity<List<CalendarEventDTO>> getEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            java.security.Principal principal) {
        // Lietotājs tiek nolasīts no Entra ID sesijas
        String user = principal.getName();
        log.info("REST pieprasījums kalendāra notikumiem no {} līdz {} lietotājam {}", start, end, user);
        return ResponseEntity.ok(calendarService.getEventsInPeriod(start, end, user));
    }

    /**
     * Pārbauda kalendāra konfliktus (Availability check).
     */
    @GetMapping("/events/check-conflict")
    public ResponseEntity<Boolean> checkConflict(
            @RequestParam String user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(calendarService.hasScheduleConflict(user, start, end));
    }

    /**
     * Izveido jaunu kalendāra notikumu.
     */
    @PostMapping("/events")
    public ResponseEntity<CalendarEventDTO> createEvent(@Valid @RequestBody CalendarEventDTO dto) {
        log.info("REST pieprasījums jauna notikuma izveidei: {}", dto.getTitle());
        return ResponseEntity.ok(calendarService.createEvent(dto));
    }

    /**
     * Daļēji atjaunina notikumu (piemēram, nomaina datumu velkot ar peli - Drag & Drop).
     */
    @PutMapping("/events/{id}")
    public ResponseEntity<CalendarEventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody CalendarEventUpdateDTO updateDTO) {
        // ŠIS LOGS MUMS PARĀDĪS, VAI DTO IR KOREKTI SAKARTĒTS
        log.info("REST pieprasījums notikuma {} atjaunināšanai. Saņemtie dati: {}", id, updateDTO);
        return ResponseEntity.ok(calendarService.partialUpdateEvent(id, updateDTO));
    }


}