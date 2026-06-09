package lv.smiltenesnkup.dvs.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Palīgklase drošības konteksta iegūšanai.
 */
public class SecurityUtils {

    /**
     * Iegūst pašreizējā autentificētā lietotāja vārdu no Spring Security konteksta.
     * @return Lietotāja vārds vai "Sistēma", ja lietotājs nav autentificēts.
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return "Sistēma";
    }

}