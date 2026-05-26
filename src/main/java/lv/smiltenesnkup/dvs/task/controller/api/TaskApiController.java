package lv.smiltenesnkup.dvs.task.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.task.dto.TaskDTO;
import lv.smiltenesnkup.dvs.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lv.smiltenesnkup.dvs.task.dto.NotificationDTO;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST kontrolieris uzdevumu izveidei un palīgdatiem (piem., lietotāju meklēšanai).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskApiController {

    private final TaskService taskService;

    /**
     * Apstrādā pieprasījumu jauna uzdevuma izveidei.
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Tiek saņemts pieprasījums jauna uzdevuma izveidei: {}", taskDTO.getTitle());
        return ResponseEntity.ok(taskService.createTask(taskDTO));
    }

    /**
     * Imitē lietotāju meklēšanu no sistēmas (Azure AD) pēc vārda fragmenta.
     * Tiek izmantots autocompletion funkcionalitātei frontend pusē.
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<String>> searchUsers(@RequestParam String query) {
        log.info("Tiek meklēti lietotāji pēc atslēgvārda: {}", query);
        // Pagaidu statisks saraksts, vēlāk tiks aizstāts ar reālu Azure AD / DB pieprasījumu
        List<String> allUsers = List.of("Jānis Bērziņš", "Ilze Kalniņa", "Pēteris Kļaviņš", "Anna Zariņa", "Lietotājs");

        List<String> filtered = allUsers.stream()
                .filter(user -> user.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtered);
    }

    // ==========================================
    // PAZIŅOJUMI (NOTIFICATIONS)
    // ==========================================

    /**
     * Atgriež visus neizlasītos paziņojumus konkrētam lietotājam.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@RequestParam String user) {
        log.info("Tiek pieprasīti neizlasītie paziņojumi lietotājam: {}", user);
        return ResponseEntity.ok(taskService.getUnreadNotifications(user));
    }

    /**
     * Atzīmē paziņojumu kā izlasītu.
     */
    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        log.info("Paziņojums ID: {} tiek atzīmēts kā izlasīts", id);
        taskService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // APAKŠUZDEVUMU STATUSI
    // ==========================================

    /**
     * Atjaunina apakšuzdevuma statusu un izsauc darbplūsmas (Workflow) loģiku.
     */
    @PutMapping("/subtasks/{id}/status")
    public ResponseEntity<Void> updateSubTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String newStatus = payload.get("status");
        String comment = payload.get("comment");

        log.info("Tiek atjaunināts apakšuzdevuma {} statuss uz {}", id, newStatus);
        taskService.updateSubTaskStatus(id, newStatus, comment);

        return ResponseEntity.ok().build();
    }

}