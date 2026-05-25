package lv.smiltenesnkup.dvs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Nodrošina globālu atribūtu pieejamību visos Thymeleaf skatos.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${app.version}")
    private String appVersion;

    /**
     * Pievieno aplikācijas versiju Thymeleaf modelim.
     */
    @ModelAttribute("appVersion")
    public String getAppVersion() {
        return appVersion;
    }

}