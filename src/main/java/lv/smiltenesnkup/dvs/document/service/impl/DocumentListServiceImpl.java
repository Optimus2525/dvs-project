package lv.smiltenesnkup.dvs.document.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;
import lv.smiltenesnkup.dvs.document.mapper.DocumentListMapper;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.document.repository.DocumentListRepository;
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
}