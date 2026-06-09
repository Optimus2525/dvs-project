package lv.smiltenesnkup.dvs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aktivizē Spring asinhrono (@Async) metožu atbalstu fona procesiem.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

}