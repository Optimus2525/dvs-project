package lv.smiltenesnkup.dvs.task.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Pārstāv sistēmas paziņojumu lietotājam.
 * Tiek izmantots, lai ģenerētu Popover paziņojumus "Mani Uzdevumi" tabulā.
 */
@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient; // Lietotājs, kuram paredzēts paziņojums

    @Column(nullable = false, columnDefinition = "text")
    private String message; // Paziņojuma teksts

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task relatedTask; // Uzdevums, par kuru ir paziņojums

    @Column(name = "is_read", nullable = false)
    private boolean read = false; // Vai lietotājs ir redzējis (aizvēris) Popover

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}