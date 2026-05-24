package lv.smiltenesnkup.dvs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;

// Pagaidām OAuth2 auto-konfigurācija tiek izslēgta, kamēr nav pievienotas Azure atslēgas
@SpringBootApplication(exclude = {OAuth2ClientAutoConfiguration.class})
public class DvsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DvsApplication.class, args);
	}

}
