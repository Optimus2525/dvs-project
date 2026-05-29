package lv.smiltenesnkup.dvs.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Pārnes sistēmas vizuālo iestatījumu datus atjaunināšanai.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UiSettingsUpdateRequestDTO {

    @NotNull(message = "Iestatījumu saraksts ir obligāts")
    private Map<String, String> settings;


}