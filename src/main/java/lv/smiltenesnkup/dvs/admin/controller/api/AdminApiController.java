package lv.smiltenesnkup.dvs.admin.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.admin.dto.DocumentListCreateRequestDTO;
import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;
import lv.smiltenesnkup.dvs.document.service.DocumentListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST kontrolieris administratora darbībām (sarakstu pārvaldībai).
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final DocumentListService documentListService;

    /**
     * Apstrādā pieprasījumu jauna saraksta un tā lauku izveidei.
     */
    @PostMapping("/lists")
    public ResponseEntity<DocumentListDTO> createListWithFields(@Valid @RequestBody DocumentListCreateRequestDTO requestDTO) {
        log.info("REST request to create list with fields: {}", requestDTO.getCode());
        DocumentListDTO createdList = documentListService.createListWithFields(requestDTO);
        return ResponseEntity.ok(createdList);
    }

}