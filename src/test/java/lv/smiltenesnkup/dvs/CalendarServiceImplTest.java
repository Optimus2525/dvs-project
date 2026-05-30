package lv.smiltenesnkup.dvs;

import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventDTO;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventUpdateDTO;
import lv.smiltenesnkup.dvs.calendar.mapper.CalendarEventMapper;
import lv.smiltenesnkup.dvs.calendar.model.CalendarEvent;
import lv.smiltenesnkup.dvs.calendar.repository.CalendarEventRepository;
import lv.smiltenesnkup.dvs.calendar.service.impl.CalendarServiceImpl;
import lv.smiltenesnkup.dvs.sharepoint.service.SharePointGraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pārbauda Kalendāra servisa biznesa loģiku.
 */
@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

    @Mock
    private CalendarEventRepository eventRepository;

    @Mock
    private CalendarEventMapper eventMapper;

    @Mock
    private SharePointGraphService graphService;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    private CalendarEvent existingEvent;

    @BeforeEach
    void setUp() {
        existingEvent = CalendarEvent.builder()
                .id(1L)
                .title("Sākotnējā Sapulce")
                .startTime(LocalDateTime.of(2026, 6, 10, 10, 0))
                .endTime(LocalDateTime.of(2026, 6, 10, 11, 0))
                .allDay(false)
                .build();
    }

    /**
     * Pārbauda, vai daļējā atjaunināšana (Partial Update) pareizi nomaina tikai norādītos laukus.
     */
    @Test
    void shouldPartiallyUpdateEvent() {
        // Arrange
        Long eventId = 1L;
        LocalDateTime newStartTime = LocalDateTime.of(2026, 6, 12, 14, 0); // Pārcelts par 2 dienām

        CalendarEventUpdateDTO updateDTO = CalendarEventUpdateDTO.builder()
                .startTime(newStartTime)
                .build();

        CalendarEventDTO expectedDto = CalendarEventDTO.builder()
                .id(eventId)
                .title("Sākotnējā Sapulce") // Virsraksts nemainās
                .startTime(newStartTime)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(CalendarEvent.class))).thenReturn(existingEvent);
        when(eventMapper.toDto(any(CalendarEvent.class))).thenReturn(expectedDto);

        // Act
        CalendarEventDTO result = calendarService.partialUpdateEvent(eventId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(newStartTime, existingEvent.getStartTime()); // Pārbauda, vai entītija tika atjaunināta
        assertEquals("Sākotnējā Sapulce", existingEvent.getTitle()); // Pārbauda, vai virsraksts nav pazudis

        verify(eventRepository, times(1)).save(existingEvent);
    }

    /**
     * Pārbauda, vai tiek izmests izņēmums, ja notikums neeksistē.
     */
    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        // Arrange
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                calendarService.partialUpdateEvent(99L, new CalendarEventUpdateDTO())
        );

        assertEquals("Notikums nav atrasts ar ID: 99", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

}