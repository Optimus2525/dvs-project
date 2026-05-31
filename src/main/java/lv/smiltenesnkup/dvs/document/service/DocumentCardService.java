package lv.smiltenesnkup.dvs.document.service;

import lv.smiltenesnkup.dvs.document.dto.DocumentCardDTO;
import lv.smiltenesnkup.dvs.document.dto.DocumentFileDTO;

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

    /**
     * Augšupielādē failu un piesaista to dokumenta kartītei.
     */
 DocumentFileDTO uploadFile(Long cardId, org.springframework.web.multipart.MultipartFile file, String uploadedBy);


}