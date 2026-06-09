package lv.smiltenesnkup.dvs.calendar.event;

/**
 * Domain Event, kas norāda, ka lokālais kalendāra notikums ir jāsinhronizē ar SharePoint.
 */
public record CalendarSyncEvent(Long eventId, String action) {

}