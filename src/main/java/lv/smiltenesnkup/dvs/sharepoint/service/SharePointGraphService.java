package lv.smiltenesnkup.dvs.sharepoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

/**
 * Bāzes serviss saziņai ar Microsoft Graph API.
 * Nodrošina centralizētu Throttling (429 kļūdu) apstrādi.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharePointGraphService {

    private final RestTemplate restTemplate = new RestTemplate(); // Vēlāk var tikt konfigurēts kā @Bean ar OAuth2 tokenu

    /**
     * Izpilda Graph API pieprasījumu ar iebūvētu "Retry-After" loģiku.
     * Ja tiek saņemta 429 Too Many Requests kļūda, pavediens (Thread) tiek pauzēts
     * uz hederī norādīto laiku, un pieprasījums tiek atkārtots.
     */
    public <T> T executeWithRetry(Supplier<ResponseEntity<T>> apiCall) {
        int maxAttempts = 3;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                attempt++;
                ResponseEntity<T> response = apiCall.get();
                return response.getBody();

            } catch (HttpClientErrorException.TooManyRequests e) {
                log.warn("Graph API Throttling (429) kļūda. Mēģinājums {} no {}", attempt, maxAttempts);

                if (attempt >= maxAttempts) {
                    log.error("Pārsniegts maksimālais Graph API atkārtojumu skaits.");
                    throw e; // Pēc 3 neveiksmīgiem mēģinājumiem metam kļūdu tālāk
                }

                // Nolasa Retry-After hederi (sekundēs)
                String retryAfterHeader = e.getResponseHeaders().getFirst(HttpHeaders.RETRY_AFTER);
                long delaySeconds = 2; // Noklusējuma aizkave, ja hederis nav norādīts

                if (retryAfterHeader != null) {
                    try {
                        delaySeconds = Long.parseLong(retryAfterHeader);
                    } catch (NumberFormatException nfe) {
                        log.warn("Neizdevās noparsēt Retry-After hederi: {}", retryAfterHeader);
                    }
                }

                log.info("Pauzē pieprasījumu uz {} sekundēm...", delaySeconds);
                try {
                    Thread.sleep(delaySeconds * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Pieprasījuma aizkave tika pārtraukta", ie);
                }
            }
        }
        return null;
    }

    // Piemērs metodei, kas izmanto šo loģiku (vēlāk šī tiks pielāgota reālajiem Graph API end-pointiem)
    /*
    public String getSharePointListItems(String listId) {
        return executeWithRetry(() ->
            restTemplate.exchange(
                "https://graph.microsoft.com/v1.0/sites/{site-id}/lists/" + listId + "/items",
                HttpMethod.GET,
                new HttpEntity<>(createHeadersWithToken()),
                String.class
            )
        );
    }
    */

}