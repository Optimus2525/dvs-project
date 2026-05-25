package lv.smiltenesnkup.dvs.task.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.task.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Apstrādā uzdevumu paneļa HTML skata pieprasījumus.
 */
@Slf4j
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskWebController {

    private final TaskService taskService;

    /**
     * Atgriež "Mani Uzdevumi" un "Sekoju līdzi" skatu.
     * Parametrs 'user' tiek izmantots testēšanai, lai simulētu dažādus pieslēgušos lietotājus.
     */
    @GetMapping("/my-tasks")
    public String showMyTasks(@RequestParam(required = false, defaultValue = "Lietotājs") String user, Model model) {
        log.info("Tiek atvērts uzdevumu skats lietotājam: {}", user);

        model.addAttribute("pageTitle", "Uzdevumi - DVS");
        model.addAttribute("currentUser", user); // Lai UI redzētu, kura lietotāja datus skatām

        // Piesaista abus sarakstus
        model.addAttribute("assignedTasks", taskService.getTasksForAssignee(user));
        model.addAttribute("followedTasks", taskService.getTasksForFollower(user));

        return "task/my-tasks";
    }
}