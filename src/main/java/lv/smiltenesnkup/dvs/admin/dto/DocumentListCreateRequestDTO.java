package lv.smiltenesnkup.dvs.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.smiltenesnkup.dvs.document.dto.FieldDefinitionDTO;

import java.util.List;

/**
 * Pārnes datus no administratora lietotāja saskarnes jauna saraksta un tā lauku izveidei.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListCreateRequestDTO {

    @NotBlank(message = "Saraksta kods ir obligāts")
    private String code;

    @NotBlank(message = "Saraksta nosaukums ir obligāts")
    private String name;

    private String description;

    @NotNull(message = "Lauku saraksts nevar būt null")
    @Valid
    private List<FieldDefinitionDTO> fields;


}