package lv.smiltenesnkup.dvs.security.repository;

import lv.smiltenesnkup.dvs.security.model.AppUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Nodrošina datu bāzes operācijas lietotāju lomām.
 */
@Repository
public interface AppUserRoleRepository extends JpaRepository<AppUserRole, Long> {

    /**
     * Atrod visas lomas konkrētam lietotājam pēc viņa vārda.
     */
    List<AppUserRole> findAllByUsername(String username);


    /**
     * Dzēš visas lomas konkrētam lietotājam.
     */
    void deleteByUsername(String username);

}