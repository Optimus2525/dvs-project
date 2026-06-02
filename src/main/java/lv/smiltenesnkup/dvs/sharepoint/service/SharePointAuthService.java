package lv.smiltenesnkup.dvs.sharepoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Nodrošina autentifikāciju pret Microsoft Entra ID, izmantojot Client Credentials plūsmu.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharePointAuthService {

    @Value("${azure.entra.tenant-id}")
    private String tenantId;

    @Value("${azure.entra.client-id}")
    private String clientId;

    @Value("${azure.entra.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Iegūst piekļuves žetonu (Access Token) no Microsoft Graph API.
     */
    public String getAccessToken() {
        log.info("Pieprasa Access Token no Microsoft Entra ID...");

        String tokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("scope", "https://graph.microsoft.com/.default");
        requestBody.add("client_secret", clientSecret);
        requestBody.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Access Token veiksmīgi saņemts!");
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            log.error("Kļūda iegūstot Access Token: {}", e.getMessage());
            throw new RuntimeException("Neizdevās autentificēties pret Microsoft Entra ID", e);
        }
        return null;
    }
}