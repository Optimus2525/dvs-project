package lv.smiltenesnkup.dvs.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pārvalda aplikācijas saknes (root) pieprasījumus un novirza lietotāju uz atbilstošo sākuma skatu.
 */
@Slf4j
@Controller
public class HomeWebController {

    /**
     * Novirza lietotāju uz "Mani uzdevumi" un Kalendāra paneli, kas ir darba dienas sākumpunkts.
     */
    @GetMapping("/")
    public String rootRedirect() {
        log.info("Lietotājs atver sistēmas saknes URL. Notiek novirzīšana uz Dashboard...");
        return "redirect:/tasks/my-tasks";
    }

}