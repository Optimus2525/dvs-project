package lv.smiltenesnkup.dvs.document.mapper;

import lv.smiltenesnkup.dvs.document.dto.DocumentFileDTO;
import lv.smiltenesnkup.dvs.document.model.DocumentFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Kartē datus starp DocumentFile entītiju un DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentFileMapper {

    @Mapping(source = "documentCard.id", target = "documentCardId")
    DocumentFileDTO toDto(DocumentFile entity);

    @Mapping(source = "documentCardId", target = "documentCard.id")
    DocumentFile toEntity(DocumentFileDTO dto);

}