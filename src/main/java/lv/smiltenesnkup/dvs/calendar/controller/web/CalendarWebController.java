package lv.smiltenesnkup.dvs.calendar.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.calendar.service.CalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Apstrādā kalendāra HTML skata pieprasījumus.
 */
@Slf4j
@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarWebController {

    private final CalendarService calendarService;

    /**
     * Atgriež galveno kalendāra skatu, piesaistot kešotās kategorijas (leģendai un filtriem).
     */
    @GetMapping
    public String showCalendar(Model model) {
        log.info("Tiek atvērts Kalendāra skats");
        model.addAttribute("pageTitle", "Kalendārs - DVS");
        model.addAttribute("categories", calendarService.getActiveCategories());
        return "calendar/calendar";
    }

}