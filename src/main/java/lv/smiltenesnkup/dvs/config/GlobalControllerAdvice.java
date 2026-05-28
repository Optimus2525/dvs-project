package lv.smiltenesnkup.dvs.config;

import lombok.RequiredArgsConstructor;
import lv.smiltenesnkup.dvs.admin.service.UiSettingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

/**
 * Nodrošina globālu atribūtu pieejamību visos Thymeleaf skatos.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @Value("${app.version}")
    private String appVersion;

    private final UiSettingService uiSettingService;

    /**
     * Pievieno aplikācijas versiju Thymeleaf modelim.
     */
    @ModelAttribute("appVersion")
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * Pievieno globālos CSS mainīgos visiem skatiem.
     */
    @ModelAttribute("uiSettings")
    public Map<String, String> getUiSettings() {
        return uiSettingService.getAllSettingsAsMap();
    }

}