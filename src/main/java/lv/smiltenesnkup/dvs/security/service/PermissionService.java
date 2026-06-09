package lv.smiltenesnkup.dvs.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.security.enums.PermissionLevel;
import lv.smiltenesnkup.dvs.security.model.DvsUser;
import lv.smiltenesnkup.dvs.security.model.ListPermission;
import lv.smiltenesnkup.dvs.security.repository.DvsUserRepository;
import lv.smiltenesnkup.dvs.security.repository.ListPermissionRepository;
import lv.smiltenesnkup.dvs.security.util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviss, kas pārbauda lietotāja tiesības veikt darbības ar dokumentu sarakstiem.
 * Tiek izmantots kopā ar @PreAuthorize anotācijām.
 */
@Slf4j
@Service("permissionService")
@RequiredArgsConstructor
public class PermissionService {

    private final DvsUserRepository dvsUserRepository;
    private final ListPermissionRepository listPermissionRepository;

    /**
     * Pārbauda, vai pašreizējam lietotājam ir vismaz norādītais piekļuves līmenis konkrētam sarakstam.
     */
    public boolean hasAccess(Long listId, String requiredLevelStr) {
        String username = SecurityUtils.getCurrentUsername();

        // Administratoriem ir piekļuve visam
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        DvsUser user = dvsUserRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        PermissionLevel requiredLevel = PermissionLevel.valueOf(requiredLevelStr);
        List<ListPermission> permissions = listPermissionRepository.findAllByDvsUserId(user.getId());

        return permissions.stream()
                .anyMatch(p -> p.getDocumentList().getId().equals(listId) &&
                        p.getPermissionLevel().ordinal() >= requiredLevel.ordinal());
    }

}