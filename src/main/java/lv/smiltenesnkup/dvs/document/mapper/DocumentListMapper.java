package lv.smiltenesnkup.dvs.document.mapper;

import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp DocumentList entītiju un DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentListMapper {

    DocumentListDTO toDto(DocumentList entity);

    DocumentList toEntity(DocumentListDTO dto);

}