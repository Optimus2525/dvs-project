package lv.smiltenesnkup.dvs.calendar.mapper;

import lv.smiltenesnkup.dvs.calendar.dto.CalendarCategoryDTO;
import lv.smiltenesnkup.dvs.calendar.model.CalendarCategory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp kalendāra kategorijas entītiju un DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarCategoryMapper {

    CalendarCategoryDTO toDto(CalendarCategory entity);

    CalendarCategory toEntity(CalendarCategoryDTO dto);


}