package lv.smiltenesnkup.dvs.security.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pārstāv lokālo DVS lietotāju.
 * Tikai šajā tabulā eksistējošie lietotāji var ielogoties sistēmā.
 */
@Entity
@Table(name = "dvs_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DvsUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

}