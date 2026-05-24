package lv.smiltenesnkup.dvs.document.mapper;

import lv.smiltenesnkup.dvs.document.dto.DocumentCardDTO;
import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp DocumentCard entītiju un DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentCardMapper {

    // Pārnes ID no saistītās entītijas uz DTO lauku
    @Mapping(source = "documentList.id", target = "documentListId")
    DocumentCardDTO toDto(DocumentCard entity);

    // Pārnes DTO ID atpakaļ uz saistīto entītiju (Hibernate to sapratīs kā Foreign Key)
    @Mapping(source = "documentListId", target = "documentList.id")
    DocumentCard toEntity(DocumentCardDTO dto);

}