package lv.smiltenesnkup.dvs.task.mapper;

import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import lv.smiltenesnkup.dvs.task.dto.SubTaskDTO;
import lv.smiltenesnkup.dvs.task.dto.TaskDTO;
import lv.smiltenesnkup.dvs.task.model.SubTask;
import lv.smiltenesnkup.dvs.task.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp Uzdevumu entītijām un DTO, izmantojot MapStruct.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "documentCard.id", target = "documentCardId")
    TaskDTO toDto(Task entity);

    // Mērķis nomainīts no "documentCard.id" uz "documentCard", lai izmantotu pielāgoto metodi
    @Mapping(source = "documentCardId", target = "documentCard")
    Task toEntity(TaskDTO dto);

    SubTaskDTO subTaskToDto(SubTask entity);

    SubTask subTaskToEntity(SubTaskDTO dto);

    /**
     * Pārveido dokumenta kartītes ID par entītiju.
     * Ja ID nav norādīts (uzdevums nav piesaistīts dokumentam), tiek atgriezta null vērtība,
     * novēršot Hibernate TransientPropertyValueException kļūdu.
     */
    default DocumentCard mapDocumentCard(Long documentCardId) {
        if (documentCardId == null) {
            return null;
        }
        DocumentCard documentCard = new DocumentCard();
        documentCard.setId(documentCardId);
        return documentCard;
    }

}