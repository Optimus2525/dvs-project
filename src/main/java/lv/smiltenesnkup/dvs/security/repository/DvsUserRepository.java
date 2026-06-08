package lv.smiltenesnkup.dvs.security.repository;

import lv.smiltenesnkup.dvs.security.model.DvsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DvsUserRepository extends JpaRepository<DvsUser, Long> {

    Optional<DvsUser> findByUsername(String username);

    /**
     * Meklē aktīvos DVS lietotājus pēc vārda fragmenta (izmantošanai UI izkrītošajos sarakstos).
     */
    @Query("SELECT u.username FROM DvsUser u WHERE u.active = true AND LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY u.username ASC")
    List<String> searchActiveUsersByUsername(@Param("query") String query);

}