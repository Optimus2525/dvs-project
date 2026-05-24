package lv.smiltenesnkup.dvs.document.service;

import lv.smiltenesnkup.dvs.document.dto.DocumentCardDTO;

import java.util.List;

public interface DocumentCardService {

    DocumentCardDTO createDocumentCard(DocumentCardDTO dto);

    List<DocumentCardDTO> getCardsByListId(Long listId);

    DocumentCardDTO getCardById(Long id);

    /**
     * Meklē dokumentus konkrētā sarakstā pēc dinamiskā metadatu lauka.
     * Piemēram: key = "Saņemšanas veids", value = "E-pasts"
     */
    List<DocumentCardDTO> searchByMetadata(Long listId, String key, String value);

}