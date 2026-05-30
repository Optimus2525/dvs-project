package lv.smiltenesnkup.dvs.calendar.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pārstāv kalendāra notikumu kategoriju un tās vizuālo krāsu.
 */
@Entity
@Table(name = "calendar_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "color_code", nullable = false)
    private String colorCode;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

}