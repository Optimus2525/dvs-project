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

}