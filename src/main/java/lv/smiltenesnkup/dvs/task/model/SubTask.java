package lv.smiltenesnkup.dvs.task.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pārstāv apakšuzdevumu, kas ir pakārtots galvenajam uzdevumam.
 * Biznesa loģikas slānī (Service) tiek ierobežots maksimālais skaits līdz 3.
 */
@Entity
@Table(name = "sub_task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_task_id", nullable = false)
    private Task parentTask;

}