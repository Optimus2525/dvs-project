package lv.smiltenesnkup.dvs.document.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.document.dto.DocumentCardDTO;
import lv.smiltenesnkup.dvs.document.mapper.DocumentCardMapper;
import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.document.repository.DocumentCardRepository;
import lv.smiltenesnkup.dvs.document.repository.DocumentListRepository;
import lv.smiltenesnkup.dvs.document.service.DocumentCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentCardServiceImpl implements DocumentCardService {

    private final DocumentCardRepository documentCardRepository;
    private final DocumentListRepository documentListRepository;
    private final DocumentCardMapper documentCardMapper;

    @Override
    @Transactional
    public DocumentCardDTO createDocumentCard(DocumentCardDTO dto) {
        log.info("Creating new document card for list ID: {}", dto.getDocumentListId());

        // Pārbauda, vai saraksts vispār eksistē
        DocumentList documentList = documentListRepository.findById(dto.getDocumentListId())
                .orElseThrow(() -> new RuntimeException("Dokumentu saraksts nav atrasts ar ID: " + dto.getDocumentListId()));

        // MapStruct automātiski saliks visus laukus, ieskaitot JSONB metadata Map objektu
        DocumentCard entity = documentCardMapper.toEntity(dto);

        // Piesaista reālo saraksta entītiju
        entity.setDocumentList(documentList);

        DocumentCard savedEntity = documentCardRepository.save(entity);
        return documentCardMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCardDTO> getCardsByListId(Long listId) {
        log.info("Fetching all document cards for list ID: {}", listId);
        return documentCardRepository.findAllByDocumentListId(listId).stream()
                .map(documentCardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentCardDTO getCardById(Long id) {
        log.info("Fetching document card with ID: {}", id);
        DocumentCard entity = documentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dokumenta kartīte nav atrasta ar ID: " + id));
        return documentCardMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCardDTO> searchByMetadata(Long listId, String key, String value) {
        log.info("Searching document cards in list {} by metadata key '{}' and value '{}'", listId, key, value);

        // Noformatē meklēšanas parametrus kā JSON stringu. Piemēram: {"Saņemšanas veids": "E-pasts"}
        // PostgreSQL @> operators sapratīs šo struktūru un meklēs to iekš JSONB kolonnas.
        String jsonQuery = String.format("{\"%s\": \"%s\"}", key, value);

        return documentCardRepository.findByMetadataContains(listId, jsonQuery).stream()
                .map(documentCardMapper::toDto)
                .collect(Collectors.toList());
    }

}