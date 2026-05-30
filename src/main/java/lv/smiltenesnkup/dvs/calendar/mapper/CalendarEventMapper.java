package lv.smiltenesnkup.dvs.calendar.mapper;

import lv.smiltenesnkup.dvs.calendar.dto.CalendarEventDTO;
import lv.smiltenesnkup.dvs.calendar.model.CalendarCategory;
import lv.smiltenesnkup.dvs.calendar.model.CalendarEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp kalendāra notikuma entītiju un DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.colorCode", target = "categoryColor")
    CalendarEventDTO toDto(CalendarEvent entity);

    @Mapping(source = "categoryId", target = "category")
    CalendarEvent toEntity(CalendarEventDTO dto);

    /**
     * Pārveido kategorijas ID par entītiju, lai izvairītos no manuālas DB meklēšanas kartēšanas laikā.
     */
    default CalendarCategory mapCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        CalendarCategory category = new CalendarCategory();
        category.setId(categoryId);
        return category;
    }


}