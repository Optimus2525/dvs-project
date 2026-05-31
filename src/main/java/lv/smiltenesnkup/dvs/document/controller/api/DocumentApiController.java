package lv.smiltenesnkup.dvs.document.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.dto.DocumentCardDTO;
import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;
import lv.smiltenesnkup.dvs.document.service.DocumentCardService;
import lv.smiltenesnkup.dvs.document.service.DocumentListService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

/**
 * REST kontrolieris dokumentu sarakstu un kartīšu datu (JSON) pārvaldībai.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentApiController {

    private final DocumentListService documentListService;
    private final DocumentCardService documentCardService;

    // ==========================================
    // DOKUMENTU SARAKSTI (Lists)
    // ==========================================

    @PostMapping("/lists")
    public ResponseEntity<DocumentListDTO> createList(@Valid @RequestBody DocumentListDTO dto) {
        log.info("REST request to create DocumentList: {}", dto.getCode());
        return ResponseEntity.ok(documentListService.createDocumentList(dto));
    }

    @GetMapping("/lists")
    public ResponseEntity<List<DocumentListDTO>> getAllLists() {
        log.info("REST request to get all DocumentLists");
        return ResponseEntity.ok(documentListService.getAllDocumentLists());
    }

    // ==========================================
    // DOKUMENTU KARTĪTES UN JSONB (Cards)
    // ==========================================

    @PostMapping("/cards")
    public ResponseEntity<DocumentCardDTO> createCard(@Valid @RequestBody DocumentCardDTO dto) {
        log.info("REST request to create DocumentCard for list ID: {}", dto.getDocumentListId());
        return ResponseEntity.ok(documentCardService.createDocumentCard(dto));
    }

    /**
     * Meklē dokumentus pēc dinamiskā JSONB lauka (piemēram, key="Saņemšanas veids", value="E-pasts")
     */
    @GetMapping("/lists/{listId}/cards/search")
    public ResponseEntity<List<DocumentCardDTO>> searchCards(
            @PathVariable Long listId,
            @RequestParam String key,
            @RequestParam String value) {
        log.info("REST request to search DocumentCards. List: {}, Key: {}, Value: {}", listId, key, value);
        return ResponseEntity.ok(documentCardService.searchByMetadata(listId, key, value));
    }


    /**
     * Apstrādā failu augšupielādi konkrētai dokumenta kartītei.
     */
    @PostMapping(value = "/cards/{cardId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<lv.smiltenesnkup.dvs.document.dto.DocumentFileDTO> uploadFile(
            @PathVariable Long cardId,
            @RequestParam("file") MultipartFile file) {
        log.info("REST pieprasījums faila augšupielādei kartītei ID: {}", cardId);

        // Pagaidām statisks lietotājs, vēlāk ņemsim no Spring Security Principal
        String uploadedBy = "Lietotājs";

        return ResponseEntity.ok(documentCardService.uploadFile(cardId, file, uploadedBy));
    }

}