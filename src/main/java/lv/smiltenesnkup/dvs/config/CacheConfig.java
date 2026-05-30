package lv.smiltenesnkup.dvs.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurē un aktivizē Spring Cache atbalstu sistēmā.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Definē noklusējuma kešatmiņas pārvaldnieku (In-Memory).
     * Tiek reģistrēts kešs "calendarCategories", ko izmanto Kalendāra serviss.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("calendarCategories");
    }

}