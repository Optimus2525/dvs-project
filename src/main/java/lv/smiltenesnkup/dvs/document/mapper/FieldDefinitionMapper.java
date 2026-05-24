package lv.smiltenesnkup.dvs.document.mapper;

import lv.smiltenesnkup.dvs.document.dto.FieldDefinitionDTO;
import lv.smiltenesnkup.dvs.document.model.FieldDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FieldDefinitionMapper {
    FieldDefinitionDTO toDto(FieldDefinition entity);

}