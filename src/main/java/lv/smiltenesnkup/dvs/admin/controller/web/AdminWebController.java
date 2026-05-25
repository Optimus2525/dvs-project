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
     * Atgriež sarakstu un metadatu lauku pārvaldības HTML skatu.
     */
    @GetMapping("/lists")
    public String showListsManagement(Model model) {
        log.info("Tiek atvērts administratora sarakstu pārvaldības skats");
        model.addAttribute("pageTitle", "Sarakstu Pārvaldība - DVS Admin");
        return "admin/lists";
    }

}