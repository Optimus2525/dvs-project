package lv.smiltenesnkup.dvs.document.service;

import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.common.exception.BusinessLogicException;
import lv.smiltenesnkup.dvs.document.enums.FieldType;
import lv.smiltenesnkup.dvs.document.model.FieldDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Nodrošina dinamisko JSONB metadatu validāciju pret saraksta lauku definīcijām.
 */
@Slf4j
@Service
public class MetadataValidationService {

    /**
     * Pārbauda, vai iesniegtie metadati atbilst saraksta konfigurācijai.
     */
    public void validateMetadata(Map<String, Object> metadata, List<FieldDefinition> fieldDefinitions) {
        log.info("Sāk JSONB metadatu validāciju...");

        for (FieldDefinition field : fieldDefinitions) {
            String fieldName = field.getName();
            Object value = metadata.get(fieldName);
            boolean isEmpty = (value == null || value.toString().trim().isEmpty());

            // 1. Pārbauda obligātos laukus
            if (field.isRequired() && isEmpty) {
                throw new BusinessLogicException(String.format("Lauks '%s' ir obligāts!", fieldName));
            }

            // Ja vērtība ir tukša, tālāka tipa validācija nav nepieciešama
            if (isEmpty) {
                continue;
            }

            // 2. Pārbauda SELECT opciju atbilstību
            if (field.getType() == FieldType.SELECT && field.getOptions() != null) {
                validateSelectOption(fieldName, value.toString(), field.getOptions());
            }

            // Šeit nākotnē var pievienot DATE formāta, NUMBER robežu un citu tipu validācijas
        }
    }

    /**
     * Pārbauda, vai ievadītā vērtība eksistē pieļaujamo opciju sarakstā.
     */
    private void validateSelectOption(String fieldName, String value, Map<String, Object> options) {
        if (options.containsKey("values")) {
            Object valuesObj = options.get("values");
            if (valuesObj instanceof List<?> allowedValues) {
                if (!allowedValues.contains(value)) {
                    throw new BusinessLogicException(
                            String.format("Vērtība '%s' nav atļauta laukam '%s'. Atļautās vērtības: %s", value, fieldName, allowedValues)
                    );
                }
            }
        }
    }

}