package lv.smiltenesnkup.dvs.security.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pārstāv lokālo lietotāja lomu DVS sistēmā (Autorizācija).
 */
@Entity
@Table(name = "app_user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "role_name", nullable = false)
    private String roleName;

}