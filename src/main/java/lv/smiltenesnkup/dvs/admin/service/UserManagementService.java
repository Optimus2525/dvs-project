package lv.smiltenesnkup.dvs.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.smiltenesnkup.dvs.admin.dto.DvsUserManageDTO;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.document.repository.DocumentListRepository;
import lv.smiltenesnkup.dvs.security.enums.PermissionLevel;
import lv.smiltenesnkup.dvs.security.model.AppUserRole;
import lv.smiltenesnkup.dvs.security.model.DvsUser;
import lv.smiltenesnkup.dvs.security.model.ListPermission;
import lv.smiltenesnkup.dvs.security.repository.AppUserRoleRepository;
import lv.smiltenesnkup.dvs.security.repository.DvsUserRepository;
import lv.smiltenesnkup.dvs.security.repository.ListPermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final DvsUserRepository dvsUserRepository;
    private final AppUserRoleRepository appUserRoleRepository;
    private final ListPermissionRepository listPermissionRepository;
    private final DocumentListRepository documentListRepository;

    @Transactional(readOnly = true)
    public List<DvsUserManageDTO> getAllUsers() {
        return dvsUserRepository.findAll().stream().map(user -> {
            DvsUserManageDTO dto = new DvsUserManageDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setActive(user.isActive());

            // Pārbauda, vai ir administrators
            dto.setAdmin(appUserRoleRepository.findAllByUsername(user.getUsername())
                    .stream().anyMatch(r -> r.getRoleName().equals("ROLE_ADMIN")));

            // Ielasa sarakstu privilēģijas
            Map<Long, String> perms = new HashMap<>();
            listPermissionRepository.findAllByDvsUserId(user.getId())
                    .forEach(lp -> perms.put(lp.getDocumentList().getId(), lp.getPermissionLevel().name()));
            dto.setListPermissions(perms);

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public DvsUserManageDTO saveOrUpdateUser(DvsUserManageDTO dto) {
        log.info("Saglabā datus DVS lietotājam: {}", dto.getUsername());

        // Atrod esošo vai izveido jaunu
        DvsUser user = dvsUserRepository.findByUsername(dto.getUsername())
                .orElseGet(() -> dvsUserRepository.save(DvsUser.builder().username(dto.getUsername()).active(true).build()));

        user.setActive(dto.isActive());
        dvsUserRepository.save(user);

        // 1. Atjaunina Administratora lomu
        appUserRoleRepository.deleteByUsername(user.getUsername());
        if (dto.isAdmin()) {
            appUserRoleRepository.save(AppUserRole.builder().username(user.getUsername()).roleName("ROLE_ADMIN").build());
        }

        // 2. Atjaunina Sarakstu privilēģijas
        listPermissionRepository.deleteByDvsUserId(user.getId());
        if (dto.getListPermissions() != null) {
            dto.getListPermissions().forEach((listId, level) -> {
                if (level != null && !level.isEmpty() && !level.equals("NONE")) {
                    DocumentList list = documentListRepository.findById(listId).orElseThrow();
                    listPermissionRepository.save(ListPermission.builder()
                            .dvsUser(user)
                            .documentList(list)
                            .permissionLevel(PermissionLevel.valueOf(level))
                            .build());
                }
            });
        }

        dto.setId(user.getId());
        return dto;
    }

}