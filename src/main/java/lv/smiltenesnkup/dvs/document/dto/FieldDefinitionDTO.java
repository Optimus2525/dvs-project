package lv.smiltenesnkup.dvs.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinitionDTO {
    private Long id;
    private String name;
    private String type;
    private Map<String, Object> options;
    private String sharepointInternalName;

}