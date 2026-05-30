package lv.smiltenesnkup.dvs.calendar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarCategoryDTO;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventDTO;
import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventUpdateDTO;
import lv.smiltenesnkup.dvs.calendar.mapper.CalendarCategoryMapper;
import lv.smiltenesnkup.dvs.calendar.mapper.CalendarEventMapper;
import lv.smiltenesnkup.dvs.calendar.model.CalendarEvent;
import lv.smiltenesnkup.dvs.calendar.repository.CalendarCategoryRepository;
import lv.smiltenesnkup.dvs.calendar.repository.CalendarEventRepository;
import lv.smiltenesnkup.dvs.calendar.service.CalendarService;
import lv.smiltenesnkup.dvs.sharepoint.service.SharePointGraphService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementē kalendāra biznesa loģiku.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarCategoryRepository categoryRepository;
    private final CalendarEventRepository eventRepository;
    private final CalendarCategoryMapper categoryMapper;
    private final CalendarEventMapper eventMapper;
    private final SharePointGraphService sharePointGraphService;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "calendarCategories") // Ieslēdzam kešatmiņu kategorijām (Rule 18)
    public List<CalendarCategoryDTO> getActiveCategories() {
        log.info("Izgūst aktīvās kalendāra kategorijas no datubāzes...");
        return categoryRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<CalendarCategoryDTO> getAllCategories() {
        log.info("Izgūst visas kalendāra kategorijas administratoram...");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "calendarCategories", allEntries = true)
    public CalendarCategoryDTO createCategory(CalendarCategoryDTO dto) {
        log.info("Izveido jaunu kategoriju: {}", dto.getName());
        lv.smiltenesnkup.dvs.calendar.model.CalendarCategory entity = categoryMapper.toEntity(dto);
        entity.setActive(true); // Pēc noklusējuma aktīva
        return categoryMapper.toDto(categoryRepository.save(entity));
    }


    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "calendarCategories", allEntries = true)
    public CalendarCategoryDTO updateCategory(Long id, CalendarCategoryDTO dto) {
        log.info("Atjaunina kategoriju ID: {}", id);
        lv.smiltenesnkup.dvs.calendar.model.CalendarCategory entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nav atrasta"));

        entity.setName(dto.getName());
        entity.setColorCode(dto.getColorCode());
        entity.setActive(dto.isActive());

        return categoryMapper.toDto(categoryRepository.save(entity));
    }


    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "calendarCategories", allEntries = true)
    public void deleteCategory(Long id) {
        log.info("Dzēš kategoriju ID: {}", id);
        // Tā kā CalendarEvent tabulā mums ir ON DELETE SET NULL, mēs varam droši dzēst no datubāzes
        categoryRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getEventsInPeriod(LocalDateTime start, LocalDateTime end) {
        log.info("Izgūst kalendāra notikumus periodam: {} - {}", start, end);
        // Šeit nākotnē var tikt izsaukta SharePointGraphService sinhronizācijas metode
        return eventRepository.findEventsInPeriod(start, end).stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public CalendarEventDTO createEvent(CalendarEventDTO dto) {
        log.info("Izveido jaunu kalendāra notikumu: {}", dto.getTitle());

        CalendarEvent entity = eventMapper.toEntity(dto);
        CalendarEvent savedEntity = eventRepository.save(entity);

        // TODO: Asinhroni nosūtīt `savedEntity` uz Graph API un atjaunināt `sharepointItemId`

        return eventMapper.toDto(savedEntity);
    }


    @Override
    @Transactional
    public CalendarEventDTO partialUpdateEvent(Long id, CalendarEventUpdateDTO updateDTO) {
        log.info("Daļēji atjaunina kalendāra notikumu ID: {}", id);

        CalendarEvent existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notikums nav atrasts ar ID: " + id));

        // Atjaunina tikai tos laukus, kas ir atsūtīti
        if(updateDTO.getTitle() != null) existingEvent.setTitle(updateDTO.getTitle());
        if (updateDTO.getDescription() != null) existingEvent.setDescription(updateDTO.getDescription());
        if (updateDTO.getStartTime() != null) existingEvent.setStartTime(updateDTO.getStartTime());
        if (updateDTO.getEndTime() != null) existingEvent.setEndTime(updateDTO.getEndTime());
        if (updateDTO.getAllDay() != null) existingEvent.setAllDay(updateDTO.getAllDay());
        if (updateDTO.getCategoryId() != null) existingEvent.setCategory(eventMapper.mapCategory(updateDTO.getCategoryId()));

        CalendarEvent savedEvent = eventRepository.save(existingEvent);

        // TODO: Asinhroni atjaunināt šīs izmaiņas arī SharePoint pusē

        return eventMapper.toDto(savedEvent);
    }


}