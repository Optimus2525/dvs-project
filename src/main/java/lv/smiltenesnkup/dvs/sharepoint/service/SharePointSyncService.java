package lv.smiltenesnkup.dvs.sharepoint.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.enums.FieldType;
import lv.smiltenesnkup.dvs.document.enums.FileRole;
import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import lv.smiltenesnkup.dvs.document.model.DocumentFile;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.document.model.FieldDefinition;
import lv.smiltenesnkup.dvs.document.repository.DocumentCardRepository;
import lv.smiltenesnkup.dvs.document.repository.DocumentFileRepository;
import lv.smiltenesnkup.dvs.document.repository.DocumentListRepository;
import lv.smiltenesnkup.dvs.document.repository.FieldDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final DocumentFileRepository documentFileRepository;

    @Autowired
    @Lazy
    private SharePointSyncService self; // Injekcija sev, lai strādātu @Transactional

    /**
     * Veic pilnu saraksta sinhronizāciju.
     * Nav @Transactional, lai nebloķētu DB savienojumu Graph API I/O laikā.
     */
    public void syncListFromSharePoint(Long internalListId) {
        log.info("Sāk sinhronizāciju sarakstam ar ID: {}", internalListId);

        DocumentList list = documentListRepository.findById(internalListId)
                .orElseThrow(() -> new RuntimeException("Saraksts nav atrasts DB!"));

        if (list.getSharepointSiteId() == null || list.getSharepointListId() == null) {
            throw new RuntimeException("Sarakstam nav norādīti SharePoint ID!");
        }

        // 1. Nolasa kolonnas no SP un saglabā DB
        JsonNode columns = graphService.getListColumns(list.getSharepointSiteId(), list.getSharepointListId());
        self.saveColumns(list, columns);

        // 2. Nolasa ierakstus no SP
        JsonNode items = graphService.getListItems(list.getSharepointSiteId(), list.getSharepointListId());

        for (JsonNode item : items) {
            String spItemId = item.get("id").asText();

            // Saglabā pamata kartīti atsevišķā DB tranzakcijā
            DocumentCard savedCard = self.saveSingleItem(list, item, spItemId);

            // Pieprasa failus no Graph API (ĀRPUS DB tranzakcijas)
            JsonNode attachments = graphService.getItemAttachments(list.getSharepointSiteId(), list.getSharepointListId(), spItemId);

            // Saglabā pielikumus atsevišķā DB tranzakcijā
            self.saveAttachments(savedCard, attachments);
        }

        log.info("Sinhronizācija veiksmīgi pabeigta sarakstam: {}", list.getName());
    }

    /**
     * Saglabā SharePoint saraksta kolonnas kā FieldDefinition.
     */
    @Transactional
    public void saveColumns(DocumentList list, JsonNode columns) {
        for (JsonNode col : columns) {
            String spInternalName = col.get("name").asText();
            String displayName = col.has("displayName") ? col.get("displayName").asText() : spInternalName;

            if (spInternalName.startsWith("_") || spInternalName.equals("Attachments") || (col.has("readOnly") && col.get("readOnly").asBoolean())) {
                continue;
            }

            FieldType type = FieldType.TEXT;
            Map<String, Object> options = null;

            if (col.has("dateTime")) type = FieldType.DATE;
            if (col.has("boolean")) type = FieldType.CHECKBOX;

            if (col.has("choice")) {
                type = FieldType.SELECT;
                JsonNode choicesNode = col.get("choice").get("choices");
                if (choicesNode != null && choicesNode.isArray()) {
                    List<String> choiceList = new ArrayList<>();
                    choicesNode.forEach(c -> choiceList.add(c.asText()));
                    options = new HashMap<>();
                    options.put("values", choiceList);
                }
            }

            FieldDefinition field = fieldDefinitionRepository.findAllByDocumentListId(list.getId()).stream()
                    .filter(f -> spInternalName.equals(f.getSharepointInternalName()))
                    .findFirst()
                    .orElse(FieldDefinition.builder()
                            .documentList(list)
                            .sharepointInternalName(spInternalName)
                            .build());

            field.setName(displayName);
            field.setType(type);
            if (options != null) {
                field.setOptions(options);
            }

            fieldDefinitionRepository.save(field);
        }
    }

    /**
     * Saglabā vienu SharePoint ierakstu lokālajā JSONB dzinējā.
     */
    @Transactional
    public DocumentCard saveSingleItem(DocumentList list, JsonNode item, String spItemId) {
        JsonNode fields = item.get("fields");

        DocumentCard card = documentCardRepository.findBySharepointItemId(spItemId)
                .orElse(DocumentCard.builder()
                        .documentList(list)
                        .sharepointItemId(spItemId)
                        .createdBy("SharePoint")
                        .metadata(new HashMap<>())
                        .build());

        card.setTitle(fields.has("Title") ? fields.get("Title").asText() : "Bez virsraksta");
        card.setDocumentNumber(null);

        Map<String, Object> metadata = card.getMetadata();
        fieldDefinitionRepository.findAllByDocumentListId(list.getId()).forEach(fieldDef -> {
            String spName = fieldDef.getSharepointInternalName();
            if (fields.has(spName) && !fields.get(spName).isNull()) {
                metadata.put(fieldDef.getName(), extractSharePointValue(fields.get(spName)));
            }
        });

        card.setMetadata(metadata);
        return documentCardRepository.save(card);
    }

    /**
     * Izvelk cilvēkam lasāmu vērtību no SharePoint JSON struktūras.
     */
    private String extractSharePointValue(JsonNode node) {
        if (node.isNull()) return "";

        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            node.forEach(n -> values.add(extractSharePointValue(n)));
            return String.join(", ", values);
        }

        if (node.isObject()) {
            if (node.has("LookupValue")) return node.get("LookupValue").asText();
            if (node.has("DisplayName")) return node.get("DisplayName").asText();
            if (node.has("Title")) return node.get("Title").asText();
            if (node.has("Email")) return node.get("Email").asText();
        }

        return node.asText();
    }

    /**
     * Saglabā dokumenta pielikumus lokālajā datubāzē.
     */
    @Transactional
    public void saveAttachments(DocumentCard card, JsonNode attachments) {
        Map<String, DocumentFile> existingSpFiles = documentFileRepository.findAllByDocumentCardId(card.getId()).stream()
                .filter(f -> f.getSharepointFileId() != null)
                .collect(Collectors.toMap(DocumentFile::getSharepointFileId, f -> f));

        for (JsonNode att : attachments) {
            String attId = att.get("id").asText();

            if (!existingSpFiles.containsKey(attId)) {
                DocumentFile newFile = DocumentFile.builder()
                        .documentCard(card)
                        .sharepointFileId(attId)
                        .fileName(att.get("name").asText())
                        .fileSize(att.has("size") ? att.get("size").asLong() : 0L)
                        .mimeType(att.has("contentType") ? att.get("contentType").asText() : "application/octet-stream")
                        .uploadedBy("SharePoint")
                        .fileRole(FileRole.ATTACHMENT)
                        .build();
                documentFileRepository.save(newFile);
            } else {
                existingSpFiles.remove(attId);
            }
        }

        if (!existingSpFiles.isEmpty()) {
            documentFileRepository.deleteAll(existingSpFiles.values());
        }
    }
}