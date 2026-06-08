package lv.smiltenesnkup.dvs.admin.dto;

import lombok.Data;
import java.util.Map;

/**
 * Pārnes datus starp Admin paneli un Backend par lietotāju, viņa lomu un sarakstu privilēģijām.
 */
@Data
public class DvsUserManageDTO {
    private Long id;
    private String username;
    private boolean active;
    private boolean admin;

    // Saraksta ID -> Privilēģijas līmenis (piem., "READ_ONLY", "EDITOR" vai "NONE")
    private Map<Long, String> listPermissions;

}