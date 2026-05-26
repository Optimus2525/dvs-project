package lv.smiltenesnkup.dvs.task.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Pārstāv apakšuzdevumu.
 * Kompleksajos uzdevumos satur paplašinātu informāciju par izpildītāju, termiņiem un statusu.
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

    @Column(columnDefinition = "text")
    private String description; // Teksta lauks, kur var ierakstīt arī atgriešanas iemeslu

    @Column(name = "assignee")
    private String assignee; // Konkrētā apakšuzdevuma atbildīgā persona

    @Column(name = "due_date")
    private LocalDate dueDate; // Apakšuzdevuma izpildes termiņš

    @Column(nullable = false)
    private String status = "Nav sākts";

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex; // Norāda secību (1, 2, 3)

    @Column(name = "is_active", nullable = false)
    private boolean active = false; // Nosaka, vai lietotājs šobrīd drīkst strādāt ar šo uzdevumu

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_task_id", nullable = false)
    private Task parentTask;

}