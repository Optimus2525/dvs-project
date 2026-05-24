package lv.smiltenesnkup.dvs.document.repository;

import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Nodrošina datu bāzes operācijas dokumentu kartītēm, ieskaitot JSONB metadatu meklēšanu.
 */
@Repository
public interface DocumentCardRepository extends JpaRepository<DocumentCard, Long> {

    /**
     * Atrod visas dokumentu kartītes konkrētam dokumentu sarakstam.
     */
    List<DocumentCard> findAllByDocumentListId(Long documentListId);


    /**
     * Native PostgreSQL vaicājums, kas meklē JSONB kolonnā.
     * Operators @> pārbauda, vai 'metadata' kolonna satur norādīto JSON struktūru.
     * cast(:jsonQuery as jsonb) ir nepieciešams, lai PostgreSQL saprastu, ka tiek sūtīts JSON, nevis parasts teksts.
     */
    @Query(value = "SELECT * FROM document_card WHERE document_list_id = :listId AND metadata @> cast(:jsonQuery as jsonb)", nativeQuery = true)
    List<DocumentCard> findByMetadataContains(@Param("listId") Long listId, @Param("jsonQuery") String jsonQuery);


}