package lv.smiltenesnkup.dvs.document.repository;

import lv.smiltenesnkup.dvs.document.model.FieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Nodrošina datu bāzes operācijas dinamisko lauku definīcijām.
 */
@Repository
public interface FieldDefinitionRepository extends JpaRepository<FieldDefinition, Long> {

    /**
     * Atrod visus laukus, kas piesaistīti konkrētam dokumentu sarakstam.
     */
    List<FieldDefinition> findAllByDocumentListId(Long documentListId);

}