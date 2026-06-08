package lv.smiltenesnkup.dvs.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.security.model.AppUserRole;
import lv.smiltenesnkup.dvs.security.model.DvsUser;
import lv.smiltenesnkup.dvs.security.repository.AppUserRoleRepository;
import lv.smiltenesnkup.dvs.security.repository.DvsUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Pielāgots serviss, kas apvieno Entra ID autentifikāciju ar lokālajām DVS lomām no datubāzes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final AppUserRoleRepository appUserRoleRepository;
    private final DvsUserRepository dvsUserRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String username = (String) oidcUser.getAttributes().get("name");

        log.info("Lietotājs {} autentificējies Entra ID. Pārbauda DVS piekļuvi...", username);

        // VĀRTU SARGA LOĢIKA: Pārbauda, vai lietotājs eksistē DVS bāzē un ir aktīvs
        DvsUser dvsUser = dvsUserRepository.findByUsername(username)
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("access_denied"), "Lietotājs nav reģistrēts DVS sistēmā"));

        if (!dvsUser.isActive()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("access_denied"), "Lietotāja konts ir bloķēts");
        }

        List<GrantedAuthority> mappedAuthorities = new ArrayList<>(oidcUser.getAuthorities());
        List<AppUserRole> localRoles = appUserRoleRepository.findAllByUsername(username);

        for (AppUserRole role : localRoles) {
            mappedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "name");
    }
}