package lv.smiltenesnkup.dvs.document.service;

import lv.smiltenesnkup.dvs.document.dto.DocumentListDTO;

import java.util.List;

/**
 * Definē biznesa loģiku darbam ar dokumentu sarakstiem.
 */
public interface DocumentListService {

    List<DocumentListDTO> getAllDocumentLists();

    DocumentListDTO getDocumentListById(Long id);

    DocumentListDTO createDocumentList(DocumentListDTO dto);

}