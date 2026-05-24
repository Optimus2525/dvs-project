package lv.smiltenesnkup.dvs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Pagaidu drošības konfigurācija izstrādes fāzei.
 * Vēlāk šeit tiks integrēts Microsoft Entra ID (OAuth2).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Izslēdzam CSRF aizsardzību, lai varam brīvi sūtīt POST pieprasījumus no IntelliJ HTTP klienta
                .csrf(AbstractHttpConfigurer::disable)
                // Atļaujam pilnīgi visus pieprasījumus bez autorizācijas
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

}