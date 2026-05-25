package lv.smiltenesnkup.dvs.task.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.task.dto.TaskDTO;
import lv.smiltenesnkup.dvs.task.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}