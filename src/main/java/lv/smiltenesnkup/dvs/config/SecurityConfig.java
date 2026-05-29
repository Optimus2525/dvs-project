package lv.smiltenesnkup.dvs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
                // Pagaidām izslēdz CSRF aizsardzību atvieglotai testēšanai
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Aizsargā visus administratora ceļus
                        .requestMatchers("/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
                        // Pārējiem ceļiem pagaidām atļauj brīvu piekļuvi
                        .anyRequest().permitAll()
                )
                // Ieslēdz iebūvēto pieteikšanās formu
                .formLogin(form -> form
                        .defaultSuccessUrl("/documents/dashboard", true)
                        .permitAll()
                )
                // Ieslēdz izrakstīšanās funkcionalitāti
                .logout(logout -> logout
                        .logoutSuccessUrl("/documents/dashboard")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Izveido pagaidu lietotājus atmiņā (In-Memory) lomu testēšanai.
     * {noop} norāda, ka parole nav šifrēta (tikai izstrādes nolūkiem).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin")
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

}