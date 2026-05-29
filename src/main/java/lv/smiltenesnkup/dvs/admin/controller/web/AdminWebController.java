package lv.smiltenesnkup.dvs.admin.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}