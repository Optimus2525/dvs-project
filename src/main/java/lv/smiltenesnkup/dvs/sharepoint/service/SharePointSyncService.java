package lv.smiltenesnkup.dvs.sharepoint.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.document.model.FieldDefinition;
import lv.smiltenesnkup.dvs.document.repository.DocumentCardRepository;
import lv.smiltenesnkup.dvs.document.repository.DocumentListRepository;
import lv.smiltenesnkup.dvs.document.repository.FieldDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Pārvalda datu sinhronizāciju starp SharePoint un lokālo DVS datubāzi.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharePointSyncService {

    private final SharePointGraphService graphService;
    private final DocumentListRepository documentListRepository;
    private final FieldDefinitionRepository fieldDefinitionRepository;
    private final DocumentCardRepository documentCardRepository;

    /**
     * Veic pilnu saraksta sinhronizāciju (kolonnas un ierakstus).
     */
    @Transactional
    public void syncListFromSharePoint(Long internalListId) {
        log.info("Sāk sinhronizāciju sarakstam ar ID: {}", internalListId);

        DocumentList list = documentListRepository.findById(internalListId)
                .orElseThrow(() -> new RuntimeException("Saraksts nav atrasts DB!"));

        if (list.getSharepointSiteId() == null || list.getSharepointListId() == null) {
            throw new RuntimeException("Sarakstam nav norādīti SharePoint ID!");
        }

        // 1. Sinhronizē kolonnas
        syncColumns(list);

        // 2. Sinhronizē ierakstus
        syncItems(list);

        log.info("Sinhronizācija veiksmīgi pabeigta sarakstam: {}", list.getName());
    }

    private void syncColumns(DocumentList list) {
        JsonNode columns = graphService.getListColumns(list.getSharepointSiteId(), list.getSharepointListId());

        for (JsonNode col : columns) {
            String spInternalName = col.get("name").asText();
            String displayName = col.has("displayName") ? col.get("displayName").asText() : spInternalName;

            // Ignorējam sistēmas kolonnas (kas sākas ar _ vai ir specifiskas)
            if (spInternalName.startsWith("_") || spInternalName.equals("Attachments") || col.has("readOnly") && col.get("readOnly").asBoolean()) {
                continue;
            }

            // Nosaka datu tipu
            String type = "TEXT";
            if (col.has("dateTime")) type = "DATE";
            if (col.has("choice")) type = "SELECT";
            if (col.has("boolean")) type = "CHECKBOX";

            // Saglabā vai atjaunina lauku datubāzē
            FieldDefinition field = fieldDefinitionRepository.findAllByDocumentListId(list.getId()).stream()
                    .filter(f -> spInternalName.equals(f.getSharepointInternalName()))
                    .findFirst()
                    .orElse(FieldDefinition.builder()
                            .documentList(list)
                            .sharepointInternalName(spInternalName)
                            .build());

            field.setName(displayName);
            field.setType(type);
            fieldDefinitionRepository.save(field);
        }
    }

    private void syncItems(DocumentList list) {
        JsonNode items = graphService.getListItems(list.getSharepointSiteId(), list.getSharepointListId());

        for (JsonNode item : items) {
            String spItemId = item.get("id").asText();
            JsonNode fields = item.get("fields");

            // Atrod esošu kartīti pēc SharePoint ID vai veido jaunu (Daudz ātrāk un optimālāk!)
            DocumentCard card = documentCardRepository.findBySharepointItemId(spItemId)
                    .orElse(DocumentCard.builder()
                            .documentList(list)
                            .sharepointItemId(spItemId)
                            .createdBy("SharePoint Sync")
                            .metadata(new HashMap<>())
                            .build());

            // Izvelk pamatdatus
            card.setTitle(fields.has("Title") ? fields.get("Title").asText() : "Bez virsraksta");
            card.setDocumentNumber("SP-" + spItemId);

            // Pārnes dinamiskos metadatus uz mūsu JSONB Map objektu
            Map<String, Object> metadata = card.getMetadata();
            fieldDefinitionRepository.findAllByDocumentListId(list.getId()).forEach(fieldDef -> {
                String spName = fieldDef.getSharepointInternalName();
                if (fields.has(spName) && !fields.get(spName).isNull()) {
                    metadata.put(fieldDef.getName(), fields.get(spName).asText());
                }
            });

            card.setMetadata(metadata);
            documentCardRepository.save(card);
        }
    }
}