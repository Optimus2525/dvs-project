package lv.smiltenesnkup.dvs.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * Pārnes dokumenta kartītes datus, iekļaujot dinamiskos metadatus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCardDTO {

    private Long id;

    @NotNull(message = "Saraksta ID jānorāda obligāti")
    private Long documentListId;

    @jakarta.validation.constraints.NotBlank(message = "Virsraksts ir obligāts")
    private String title;

    private String documentNumber;

    @NotBlank(message = "Dokumenta veidotājs jānorāda obligāti")
    private String createdBy;

    private String sharepointItemId;

    private List<DocumentFileDTO> files;

    @NotNull(message = "Dokumenta metadati nevar būt tukši")
    private Map<String, Object> metadata;

}