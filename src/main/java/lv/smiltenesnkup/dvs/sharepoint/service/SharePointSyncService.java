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

        // 2. Sinhronizē ierakstus un to pielikumus
        syncItems(list);

        log.info("Sinhronizācija veiksmīgi pabeigta sarakstam: {}", list.getName());
    }


    /**
     * Sinhronizē SharePoint saraksta kolonnas un saglabā tās kā FieldDefinition.
     */
    private void syncColumns(DocumentList list) {
        JsonNode columns = graphService.getListColumns(list.getSharepointSiteId(), list.getSharepointListId());

        for (JsonNode col : columns) {
            String spInternalName = col.get("name").asText();
            String displayName = col.has("displayName") ? col.get("displayName").asText() : spInternalName;

            // Ignorē sistēmas kolonnas (kas sākas ar _ vai ir specifiskas)
            if (spInternalName.startsWith("_") || spInternalName.equals("Attachments") || (col.has("readOnly") && col.get("readOnly").asBoolean())) {
                continue;
            }

            // Nosaka datu tipu, izmantojot stingro Enum
            FieldType type = FieldType.TEXT;
            Map<String, Object> options = null;

            if (col.has("dateTime")) type = FieldType.DATE;
            if (col.has("boolean")) type = FieldType.CHECKBOX;

            // Parsē Choice (Izvēlņu) opcijas, lai atjauninātu Dropdown vērtības
            if (col.has("choice")) {
                type = lv.smiltenesnkup.dvs.document.enums.FieldType.SELECT;
                JsonNode choicesNode = col.get("choice").get("choices");
                if (choicesNode != null && choicesNode.isArray()) {
                    List<String> choiceList = new ArrayList<>();
                    choicesNode.forEach(c -> choiceList.add(c.asText()));
                    options = new HashMap<>();
                    options.put("values", choiceList);
                }
            }

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
            if (options != null) {
                field.setOptions(options);
            }

            fieldDefinitionRepository.save(field);
        }
    }


    /**
     * Sinhronizē SharePoint saraksta ierakstus un saglabā tos lokālajā JSONB dzinējā.
     */
    private void syncItems(DocumentList list) {
        JsonNode items = graphService.getListItems(list.getSharepointSiteId(), list.getSharepointListId());

        for (JsonNode item : items) {
            String spItemId = item.get("id").asText();
            JsonNode fields = item.get("fields");

            // Atrod esošu kartīti pēc SharePoint ID vai veido jaunu
            DocumentCard card = documentCardRepository.findBySharepointItemId(spItemId)
                    .orElse(DocumentCard.builder()
                            .documentList(list)
                            .sharepointItemId(spItemId)
                            .createdBy("SharePoint")
                            .metadata(new HashMap<>())
                            .build());

            // Izvelk pamatdatus (SharePoint Title kļūst par galveno virsrakstu)
            card.setTitle(fields.has("Title") ? fields.get("Title").asText() : "Bez virsraksta");

            // Autonumerāciju vairs neģenerē lokāli, atstāj null
            card.setDocumentNumber(null);

            // Pārnes dinamiskos metadatus uz JSONB Map objektu
            Map<String, Object> metadata = card.getMetadata();
            fieldDefinitionRepository.findAllByDocumentListId(list.getId()).forEach(fieldDef -> {
                String spName = fieldDef.getSharepointInternalName();
                if (fields.has(spName) && !fields.get(spName).isNull()) {
                    metadata.put(fieldDef.getName(), extractSharePointValue(fields.get(spName)));
                }
            });

            card.setMetadata(metadata);
            DocumentCard savedCard = documentCardRepository.save(card);

            // Sinhronizē pielikumus šai kartītei
            syncAttachments(list, savedCard, spItemId);
        }
    }


    /**
     * Izvelk cilvēkam lasāmu vērtību no SharePoint JSON struktūras.
     * Apstrādā Lookup, Person/Group un Multi-Select laukus.
     */
    private String extractSharePointValue(JsonNode node) {
        if (node.isNull()) return "";

        // Ja tas ir masīvs (Multi-select Person vai Multi-select Lookup)
        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            node.forEach(n -> values.add(extractSharePointValue(n)));
            return String.join(", ", values);
        }

        // Ja tas ir objekts (Person vai Lookup)
        if (node.isObject()) {
            if (node.has("LookupValue")) return node.get("LookupValue").asText();
            if (node.has("DisplayName")) return node.get("DisplayName").asText();
            if (node.has("Title")) return node.get("Title").asText();
            if (node.has("Email")) return node.get("Email").asText();
        }

        // Standarta teksta, skaitļa vai datuma vērtība
        return node.asText();
    }


    /**
     * Sinhronizē dokumenta pielikumus no SharePoint uz lokālo datubāzi.
     */
    private void syncAttachments(DocumentList list, DocumentCard card, String spItemId) {
        JsonNode attachments = graphService.getItemAttachments(list.getSharepointSiteId(), list.getSharepointListId(), spItemId);

        // Iegūst esošos SP failus no datubāzes, lai nedublētu
        Map<String, DocumentFile> existingSpFiles = documentFileRepository.findAllByDocumentCardId(card.getId()).stream()
                .filter(f -> f.getSharepointFileId() != null)
                .collect(Collectors.toMap(DocumentFile::getSharepointFileId, f -> f));

        for (JsonNode att : attachments) {
            String attId = att.get("id").asText();

            if (!existingSpFiles.containsKey(attId)) {
                // Fails ir jauns
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
                // Fails jau eksistē, izņem no mapes, lai beigās zinātu, ko dzēst
                existingSpFiles.remove(attId);
            }
        }

        // Dzēš failus, kas vairs neeksistē SharePoint sistēmā
        if (!existingSpFiles.isEmpty()) {
            documentFileRepository.deleteAll(existingSpFiles.values());
        }
    }

}