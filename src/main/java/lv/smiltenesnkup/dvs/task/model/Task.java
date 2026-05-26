package lv.smiltenesnkup.dvs.task.model;

import jakarta.persistence.*;
import lombok.*;
import lv.smiltenesnkup.dvs.document.model.DocumentCard;
import lv.smiltenesnkup.dvs.task.enums.TaskPriority;
import lv.smiltenesnkup.dvs.task.enums.TaskType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

/**
 * Pārstāv sistēmas uzdevumu (Workflow), kas var būt piesaistīts dokumentam vai būt neatkarīgs.
 */
@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String assignee; // Atbildīgā persona

    // Glabā lietotāju sarakstu (Seko līdzi) JSONB formātā, izmantojot Hypersistence Utils
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> followers;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.NORMAL;

    @Column(nullable = false)
    private String status = "Nav sākts";

    // Opcionāla saistība ar dokumentu kartīti
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_card_id")
    private DocumentCard documentCard;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType = TaskType.REGULAR;

}