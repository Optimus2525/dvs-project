package lv.smiltenesnkup.dvs.admin.repository;

import lv.smiltenesnkup.dvs.admin.model.UiSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Nodrošina datu bāzes operācijas sistēmas vizuālajiem iestatījumiem.
 */
@Repository
public interface UiSettingRepository extends JpaRepository<UiSetting, String> {

}