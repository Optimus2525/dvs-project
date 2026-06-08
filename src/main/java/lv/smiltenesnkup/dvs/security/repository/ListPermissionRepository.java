package lv.smiltenesnkup.dvs.security.repository;

import lv.smiltenesnkup.dvs.security.model.ListPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListPermissionRepository extends JpaRepository<ListPermission, Long> {

    List<ListPermission> findAllByDvsUserId(Long userId);

    void deleteByDvsUserId(Long userId);

}