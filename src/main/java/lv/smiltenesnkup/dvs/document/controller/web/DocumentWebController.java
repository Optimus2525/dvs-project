package lv.smiltenesnkup.dvs.document.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.service.DocumentCardService;
import lv.smiltenesnkup.dvs.document.service.DocumentListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Kontrolieris, kas atbild par Thymeleaf HTML skatu atgriešanu lietotājam.
 */
@Slf4j
@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentWebController {

    private final DocumentListService documentListService;
    private final DocumentCardService documentCardService;

    /**
     * Apstrādā pieprasījumu un atgriež galvenā paneļa (dashboard) HTML skatu.
     * Piesaista skatam konkrētā saraksta dinamiskos laukus un dokumentus.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        log.info("Lietotājs atver DVS Dashboard skatu");

        Long listId = 1L; // Cieti iekodēts testa saraksta ID

        model.addAttribute("pageTitle", "Mans Panelis - DVS");
        model.addAttribute("listId", listId);
        model.addAttribute("dynamicFields", documentListService.getFieldsByListId(listId));

        // Tiek iegūtas un padotas modelim visas šī saraksta kartītes
        model.addAttribute("documentCards", documentCardService.getCardsByListId(listId));

        return "document/dashboard";
    }


    /**
     * Atver konkrēta dokumenta kartītes detalizēto skatu.
     */
    @GetMapping("/cards/{id}")
    public String showCardDetail(@PathVariable Long id, Model model) {
        log.info("Lietotājs atver dokumenta kartīti ID: {}", id);

        lv.smiltenesnkup.dvs.document.dto.DocumentCardDTO card = documentCardService.getCardById(id);

        model.addAttribute("pageTitle", "Dokuments " + card.getDocumentNumber());
        model.addAttribute("card", card);
        // Padodam arī saraksta lauku definīcijas, lai zinātu, kā saucas metadati
        model.addAttribute("dynamicFields", documentListService.getFieldsByListId(card.getDocumentListId()));

        return "document/card-detail";
    }

}
