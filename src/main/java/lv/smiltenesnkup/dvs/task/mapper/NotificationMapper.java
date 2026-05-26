package lv.smiltenesnkup.dvs.task.mapper;

import lv.smiltenesnkup.dvs.task.dto.NotificationDTO;
import lv.smiltenesnkup.dvs.task.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp Paziņojumu entītiju un DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(source = "relatedTask.id", target = "relatedTaskId")
    NotificationDTO toDto(Notification entity);

}