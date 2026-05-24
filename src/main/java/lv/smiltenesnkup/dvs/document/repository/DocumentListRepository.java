package lv.smiltenesnkup.dvs.document.repository;

import lv.smiltenesnkup.dvs.document.model.DocumentList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Nodrošina datu bāzes operācijas dokumentu sarakstu entītijai.
 */
@Repository
public interface DocumentListRepository extends JpaRepository<DocumentList, Long> {

}