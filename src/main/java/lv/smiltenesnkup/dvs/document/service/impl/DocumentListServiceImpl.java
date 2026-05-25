package lv.smiltenesnkup.dvs.document.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.admin.dto.DocumentListCreateRequestDTO;
import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;
import lv.smiltenesnkup.dvs.document.dto.FieldDefinitionDTO;
import lv.smiltenesnkup.dvs.document.mapper.DocumentListMapper;
import lv.smiltenesnkup.dvs.document.mapper.FieldDefinitionMapper;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.document.repository.DocumentListRepository;
import lv.smiltenesnkup.dvs.document.repository.FieldDefinitionRepository;
import lv.smiltenesnkup.dvs.document.service.DocumentListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementē biznesa loģiku dokumentu sarakstiem.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentListServiceImpl implements DocumentListService {

    private final DocumentListRepository documentListRepository;
    private final DocumentListMapper documentListMapper;
    private final FieldDefinitionRepository fieldDefinitionRepository;
    private final FieldDefinitionMapper fieldDefinitionMapper;

    /**
     * Izgūst visus sistēmā reģistrētos dokumentu sarakstus.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DocumentListDTO> getAllDocumentLists() {
        log.info("Fetching all document lists");
        return documentListRepository.findAll().stream()
                .map(documentListMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Izgūst konkrētu dokumentu sarakstu pēc tā identifikatora.
     */
    @Override
    @Transactional(readOnly = true)
    public DocumentListDTO getDocumentListById(Long id) {
        log.info("Fetching document list with ID: {}", id);
        DocumentList documentList = documentListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dokumentu saraksts nav atrasts ar ID: " + id)); // Vēlāk šis tiks aizstāts ar globālo Exception

        return documentListMapper.toDto(documentList);
    }

    /**
     * Izveido un saglabā jaunu dokumentu sarakstu.
     */
    @Override
    @Transactional
    public DocumentListDTO createDocumentList(DocumentListDTO dto) {
        log.info("Creating new document list with code: {}", dto.getCode());
        DocumentList entity = documentListMapper.toEntity(dto);
        DocumentList savedEntity = documentListRepository.save(entity);

        return documentListMapper.toDto(savedEntity);
    }

    /**
     * Izveido sarakstu un tam piesaistītos laukus vienas datubāzes tranzakcijas ietvaros.
     */
    @Override
    @Transactional
    public DocumentListDTO createListWithFields(lv.smiltenesnkup.dvs.admin.dto.DocumentListCreateRequestDTO requestDTO) {
        log.info("Creating list {} with {} fields", requestDTO.getCode(), requestDTO.getFields().size());

        // Izveido pamatentītiju
        DocumentList listEntity = DocumentList.builder()
                .code(requestDTO.getCode())
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .build();

        DocumentList savedList = documentListRepository.save(listEntity);

        // Sagatavo un saglabā lauku definīcijas
        List<lv.smiltenesnkup.dvs.document.model.FieldDefinition> fieldEntities = requestDTO.getFields().stream()
                .map(fieldDto -> lv.smiltenesnkup.dvs.document.model.FieldDefinition.builder()
                        .documentList(savedList)
                        .name(fieldDto.getName())
                        .type(fieldDto.getType())
                        .build())
                .collect(Collectors.toList());

        fieldDefinitionRepository.saveAll(fieldEntities);

        return documentListMapper.toDto(savedList);
    }

    /**
     * Izgūst visus dinamiskos laukus no datubāzes konkrētam sarakstam un pārveido tos uz DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public List<lv.smiltenesnkup.dvs.document.dto.FieldDefinitionDTO> getFieldsByListId(Long listId) {
        log.info("Fetching dynamic fields for list ID: {}", listId);
        return fieldDefinitionRepository.findAllByDocumentListId(listId).stream()
                .map(fieldDefinitionMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

}