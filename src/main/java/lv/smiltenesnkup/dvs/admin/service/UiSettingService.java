package lv.smiltenesnkup.dvs.admin.service;

import lombok.RequiredArgsConstructor;
import lv.smiltenesnkup.dvs.admin.model.UiSetting;
import lv.smiltenesnkup.dvs.admin.repository.UiSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviss darbam ar sistēmas vizuālajiem iestatījumiem.
 */
@Service
@RequiredArgsConstructor
public class UiSettingService {

    private final UiSettingRepository uiSettingRepository;

    /**
     * Izgūst visus iestatījumus un pārveido tos par Map objektu ērtai lietošanai Thymeleaf.
     */
    @Transactional(readOnly = true)
    public Map<String, String> getAllSettingsAsMap() {
        return uiSettingRepository.findAll().stream()
                .collect(Collectors.toMap(UiSetting::getSettingKey, UiSetting::getSettingValue));
    }

    /**
     * Atjaunina norādītos vizuālos iestatījumus datubāzē. Ja tāds iestatījums vēl neeksistē, tas tiek izveidots.
     */
    @Transactional
    public void updateSettings(Map<String, String> updatedSettings) {
        updatedSettings.forEach((key, value) -> {
            // Atrod esošo iestatījumu vai izveido jaunu, ja tas datubāzē neeksistē
            UiSetting setting = uiSettingRepository.findById(key)
                    .orElse(UiSetting.builder().settingKey(key).build());

            setting.setSettingValue(value);
            uiSettingRepository.save(setting);
        });
    }

}