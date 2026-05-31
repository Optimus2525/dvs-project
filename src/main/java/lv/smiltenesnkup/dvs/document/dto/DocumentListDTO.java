package lv.smiltenesnkup.dvs.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pārnes dokumentu saraksta datus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListDTO {

    private Long id;

    @NotBlank(message = "Saraksta kods jānorāda obligāti")
    private String code;

    @NotBlank(message = "Saraksta nosaukums jānorāda obligāti")
    private String name;

    private String description;

    private String sharepointSiteId;

    private String sharepointListId;

}