package lv.smiltenesnkup.dvs.document.repository;

import lv.smiltenesnkup.dvs.document.model.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long> {

    List<DocumentFile> findAllByDocumentCardId(Long documentCardId);

}