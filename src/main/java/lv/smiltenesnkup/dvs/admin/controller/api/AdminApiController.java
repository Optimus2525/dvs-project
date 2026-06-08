package lv.smiltenesnkup.dvs.admin.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.admin.dto.DocumentListCreateRequestDTO;
import lv.smiltenesnkup.dvs.admin.dto.DvsUserManageDTO;
import lv.smiltenesnkup.dvs.admin.service.UserManagementService;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarCategoryDTO;
import lv.smiltenesnkup.dvs.calendar.service.CalendarService;
import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;
import lv.smiltenesnkup.dvs.document.service.DocumentListService;
import lv.smiltenesnkup.dvs.sharepoint.service.SharePointGraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lv.smiltenesnkup.dvs.admin.dto.UiSettingsUpdateRequestDTO;
import lv.smiltenesnkup.dvs.admin.service.UiSettingService;
import lv.smiltenesnkup.dvs.sharepoint.service.SharePointSyncService;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final DocumentListService documentListService;
    private final UiSettingService uiSettingService;
    private final CalendarService calendarService;
    private final SharePointSyncService sharePointSyncService;
    private final UserManagementService userManagementService;
    private final SharePointGraphService sharePointGraphService;


    // --- LIETOTĀJU PĀRVALDĪBA ---

    @GetMapping("/users/search-entra")
    public ResponseEntity<java.util.List<String>> searchEntraUsers(@RequestParam String query) {
        log.info("Admin meklē lietotāju Entra ID Graph API: {}", query);
        // ŠIS sauc Graph API, atšķirībā no TaskApiController, kas sauc lokālo DB
        return ResponseEntity.ok(sharePointGraphService.searchUsers(query));
    }


    @GetMapping("/users")
    public ResponseEntity<java.util.List<DvsUserManageDTO>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }


    @PostMapping("/users")
    public ResponseEntity<DvsUserManageDTO> saveUser(@RequestBody DvsUserManageDTO dto) {
        return ResponseEntity.ok(userManagementService.saveOrUpdateUser(dto));
    }


    // --- KALENDĀRA KATEGORIJAS ---

    @GetMapping("/calendar-categories")
    public ResponseEntity<java.util.List<CalendarCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(calendarService.getAllCategories());
    }


    @PostMapping("/calendar-categories")
    public ResponseEntity<CalendarCategoryDTO> createCategory(@Valid @RequestBody CalendarCategoryDTO dto) {
        return ResponseEntity.ok(calendarService.createCategory(dto));
    }


    @PutMapping("/calendar-categories/{id}")
    public ResponseEntity<CalendarCategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CalendarCategoryDTO dto) {
        return ResponseEntity.ok(calendarService.updateCategory(id, dto));
    }


    @DeleteMapping("/calendar-categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        calendarService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }


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


    /**
     * Manuāli iedarbina SharePoint sinhronizāciju konkrētam sarakstam (PoC testēšanai).
     */
    @PostMapping("/sharepoint/sync/{listId}")
    public ResponseEntity<String> syncSharePointList(@PathVariable Long listId) {
        log.info("REST pieprasījums SharePoint sinhronizācijai sarakstam ID: {}", listId);
        try {
            sharePointSyncService.syncListFromSharePoint(listId);
            return ResponseEntity.ok("Sinhronizācija veiksmīga!");
        } catch (Exception e) {
            log.error("Sinhronizācijas kļūda", e);
            return ResponseEntity.internalServerError().body("Kļūda: " + e.getMessage());
        }
    }

}