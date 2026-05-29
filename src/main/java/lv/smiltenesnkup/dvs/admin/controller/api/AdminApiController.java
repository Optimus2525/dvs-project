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
import lv.smiltenesnkup.dvs.admin.dto.UiSettingsUpdateRequestDTO;
import lv.smiltenesnkup.dvs.admin.service.UiSettingService;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final DocumentListService documentListService;
    private final UiSettingService uiSettingService;

    /**
     * Atjaunina sistēmas vizuālos (CSS) iestatījumus.
     */
    @PutMapping("/settings/ui")
    public ResponseEntity<Void> updateUiSettings(@Valid @RequestBody UiSettingsUpdateRequestDTO requestDTO) {
        log.info("REST pieprasījums vizuālo iestatījumu atjaunināšanai");
        uiSettingService.updateSettings(requestDTO.getSettings());
        return ResponseEntity.ok().build();
    }

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