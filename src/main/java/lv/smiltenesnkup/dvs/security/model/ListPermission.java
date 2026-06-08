package lv.smiltenesnkup.dvs.security.model;

import jakarta.persistence.*;
import lombok.*;
import lv.smiltenesnkup.dvs.document.model.DocumentList;
import lv.smiltenesnkup.dvs.security.enums.PermissionLevel;

/**
 * Sasaista DVS lietotāju ar dokumentu sarakstu un nosaka viņa piekļuves līmeni.
 */
@Entity
@Table(name = "list_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dvs_user_id", nullable = false)
    private DvsUser dvsUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_list_id", nullable = false)
    private DocumentList documentList;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false)
    private PermissionLevel permissionLevel;

}