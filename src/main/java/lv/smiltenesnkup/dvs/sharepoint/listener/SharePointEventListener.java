package lv.smiltenesnkup.dvs.sharepoint.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.calendar.event.CalendarSyncEvent;
import lv.smiltenesnkup.dvs.sharepoint.service.SharePointGraphService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Fona (Async) klausītājs, kas uztver sistēmas notikumus un sazinās ar Graph API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SharePointEventListener {

    private final SharePointGraphService graphService;

    @Async
    @EventListener
    public void handleCalendarSync(CalendarSyncEvent event) {
        log.info("Asinhroni sūta kalendāra notikumu ID: {} uz SharePoint (Darbība: {})", event.eventId(), event.action());

        try {
            // Šeit nākotnē būs reālais graphService.createCalendarEvent(...) izsaukums
            Thread.sleep(1000); // Imitējam tīkla aizkavi
            log.info("Notikums veiksmīgi nosūtīts uz SharePoint!");
        } catch (Exception e) {
            log.error("Kļūda sinhronizējot notikumu ar SharePoint", e);
        }
    }

}