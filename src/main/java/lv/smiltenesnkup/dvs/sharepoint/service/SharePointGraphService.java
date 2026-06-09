package lv.smiltenesnkup.dvs.sharepoint.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.sharepoint.exception.SharePointSyncException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

/**
 * Bāzes serviss saziņai ar Microsoft Graph API.
 * Nodrošina centralizētu Throttling (429 kļūdu) apstrādi un datu ielasīšanu.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharePointGraphService {


    private final SharePointAuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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


    /**
     * Izveido HTTP hederus ar iegūto Access Token.
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authService.getAccessToken());
        headers.set("Accept", "application/json");
        return headers;
    }


    /**
     * Meklē Microsoft Entra ID lietotājus pēc vārda vai e-pasta fragmenta.
     */
    public java.util.List<String> searchUsers(String query) {
        // Aizsargā pret Graph API sintakses kļūdām, ja ievadē ir apostrofs
        String safeQuery = query.replace("'", "''");

        // Meklē pēc Vārda Uzvārda vai E-pasta, atgriežot maksimāli 10 rezultātus
        String url = String.format("https://graph.microsoft.com/v1.0/users?$filter=startswith(displayName,'%s') or startswith(userPrincipalName,'%s')&$select=displayName,userPrincipalName&$top=10", safeQuery, safeQuery);

        log.info("Pieprasa lietotājus no Graph API: {}", url);

        java.util.List<String> users = new java.util.ArrayList<>();
        try {
            String response = executeWithRetry(() ->
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class)
            );

            JsonNode valueNode = objectMapper.readTree(response).get("value");
            if (valueNode != null && valueNode.isArray()) {
                for (JsonNode node : valueNode) {
                    if (node.has("displayName")) {
                        users.add(node.get("displayName").asText());
                    }
                }
            }
        } catch (org.springframework.web.client.HttpClientErrorException.Forbidden e) {
            // Noķer 403 kļūdu, lai nebrūk visa sistēma, un izvada brīdinājumu
            log.warn("Nav tiesību (403 Forbidden) lasīt Entra ID lietotājus. Pārbaudi 'User.Read.All' (Application) tiesības Azure Portal! Detaļas: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Kļūda parsējot Graph API atbildi lietotāju meklēšanai", e);
        }

        return users; // Ja ir kļūda, atgriež tukšu sarakstu, lai UI var turpināt darbu
    }


    /**
     * Nolasa visas kolonnas (lauku definīcijas) no konkrēta SharePoint saraksta.
     */
    public JsonNode getListColumns(String siteId, String listId) {
        String url = String.format("https://graph.microsoft.com/v1.0/sites/%s/lists/%s/columns", siteId, listId);
        log.info("Pieprasa kolonnas no SharePoint saraksta: {}", url);

        String response = executeWithRetry(() ->
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class)
        );

        try {
            return objectMapper.readTree(response).get("value");
        } catch (Exception e) {
            throw new RuntimeException("Kļūda parsējot Graph API atbildi kolonnām", e);
        }
    }


    /**
     * Nolasa visus ierakstus no konkrēta SharePoint saraksta, iekļaujot to dinamiskos metadatus (fields).
     */
    public JsonNode getListItems(String siteId, String listId) {
        String url = String.format("https://graph.microsoft.com/v1.0/sites/%s/lists/%s/items?expand=fields", siteId, listId);
        log.info("Pieprasa ierakstus no SharePoint saraksta: {}", url);

        String response = executeWithRetry(() ->
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class)
        );

        try {
            return objectMapper.readTree(response).get("value");
        } catch (Exception e) {
            throw new RuntimeException("Kļūda parsējot Graph API atbildi ierakstiem", e);
        }
    }


    /**
     * Nolasa visus pielikumus (failus), kas piesaistīti konkrētam SharePoint saraksta ierakstam.
     */
    public JsonNode getItemAttachments(String siteId, String listId, String itemId) {
        String url = String.format("https://graph.microsoft.com/v1.0/sites/%s/lists/%s/items/%s/attachments", siteId, listId, itemId);
        log.info("Pieprasa pielikumus no SharePoint ieraksta: {}", url);

        try {
            String response = executeWithRetry(() ->
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class)
            );
            return objectMapper.readTree(response).get("value");

        } catch (HttpClientErrorException e) {
            // Ja tas ir 400 Bad Request (piem., dokumentu bibliotēka) vai 404 Not Found, mēs vienkārši atgriežam tukšu sarakstu
            if (e.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST) ||
                    e.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                log.warn("Pielikumi nav atbalstīti vai nav atrasti ierakstam {}. Turpina bez pielikumiem.", itemId);
                return objectMapper.createArrayNode();
            }
            // Ja kļūda ir cita (piemēram, 401 Unauthorized vai 403 Forbidden), tad gan metam izņēmumu
            log.error("Kritiska HTTP kļūda pieprasot pielikumus ierakstam {}: {}", itemId, e.getMessage());
            throw new SharePointSyncException("HTTP kļūda pieprasot pielikumus ierakstam: " + itemId, e);
        } catch (Exception e) {
            log.error("Kļūda parsējot Graph API atbildi pielikumiem", e);
            throw new SharePointSyncException("Kļūda parsējot Graph API atbildi pielikumiem ierakstam: " + itemId, e);
        }
    }


}