package lv.smiltenesnkup.dvs.admin.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.service.DocumentListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Apstrādā administratora paneļa skatu pieprasījumus.
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final DocumentListService documentListService;

    /**
     * Atgriež Administratora galvenā paneļa skatu.
     */
    @GetMapping({"", "/", "/dashboard"})
    public String showAdminDashboard(Model model) {
        log.info("Tiek atvērts administratora galvenais panelis");
        model.addAttribute("pageTitle", "Administratora Panelis - DVS");
        return "admin/dashboard";
    }


    /**
     * Atgriež sarakstu un metadatu lauku pārvaldības HTML skatu.
     */
    @GetMapping("/lists")
    public String showListsManagement(Model model) {
        log.info("Tiek atvērts administratora sarakstu pārvaldības skats");
        model.addAttribute("pageTitle", "Sarakstu Pārvaldība - DVS Admin");
        return "admin/lists";
    }


    /**
     * Atgriež sistēmas vizuālo iestatījumu pārvaldības HTML skatu.
     */
    @GetMapping("/settings")
    public String showSettings(Model model) {
        log.info("Tiek atvērts administratora iestatījumu skats");
        model.addAttribute("pageTitle", "Sistēmas Iestatījumi - DVS Admin");
        // Piezīme: 'uiSettings' Map objekts tiek automātiski pievienots modelim caur GlobalControllerAdvice
        return "admin/settings";
    }


    @GetMapping("/calendar-categories")
    public String showCalendarCategories(Model model) {
        log.info("Tiek atvērts administratora kalendāra kategoriju skats");
        model.addAttribute("pageTitle", "Kalendāra Kategorijas - DVS Admin");
        return "admin/calendar-categories";
    }


    @GetMapping("/users")
    public String showUsersManagement(Model model) {
        log.info("Tiek atvērts lietotāju pārvaldības skats");
        model.addAttribute("pageTitle", "Lietotāju Pārvaldība - DVS Admin");
        // Padodam visus sarakstus, lai UI varētu sazīmēt tiesību tabulu
        model.addAttribute("documentLists", documentListService.getAllDocumentLists());
        return "admin/users";
    }

}