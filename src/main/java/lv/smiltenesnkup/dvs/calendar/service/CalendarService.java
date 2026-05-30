package lv.smiltenesnkup.dvs.calendar.service;

import lv.smiltenesnkup.dvs.calendar.dto.CalendarCategoryDTO;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventDTO;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Definē biznesa loģiku darbam ar kalendāra moduļa datiem.
 */
public interface CalendarService {

    /**
     * Izgūst visas aktīvās kalendāra kategorijas (kešots).
     */
    List<CalendarCategoryDTO> getActiveCategories();

    /**
     * Izgūst visas kalendāra kategorijas (arī neaktīvās) administratora panelim.
     */
    List<CalendarCategoryDTO> getAllCategories();

    CalendarCategoryDTO createCategory(CalendarCategoryDTO dto);

    CalendarCategoryDTO updateCategory(Long id, CalendarCategoryDTO dto);

    void deleteCategory(Long id);

    /**
     * Izgūst visus notikumus norādītajā laika periodā (piem., aktuālajā mēnesī).
     */
    List<CalendarEventDTO> getEventsInPeriod(LocalDateTime start, LocalDateTime end);

    /**
     * Izveido jaunu lokālo notikumu un asinhroni nosūta to uz SharePoint.
     */
    CalendarEventDTO createEvent(CalendarEventDTO dto);

    /**
     * Daļēji atjaunina notikuma datus (piemēram, mainot datumu kalendāra skatā).
     */
    CalendarEventDTO partialUpdateEvent(Long id, CalendarEventUpdateDTO updateDTO);


}