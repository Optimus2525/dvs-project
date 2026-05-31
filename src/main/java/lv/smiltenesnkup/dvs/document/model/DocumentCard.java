package lv.smiltenesnkup.dvs.document.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Map;

/**
 * Pārstāv konkrētu dokumenta kartīti ar tās dinamiskajiem metadatiem.
 */
@Entity
@Table(name = "document_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_list_id", nullable = false)
    private DocumentList documentList;

    @Column(nullable = false)
    private String title;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "sharepoint_item_id")
    private String sharepointItemId;

    @OneToMany(mappedBy = "documentCard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentFile> files = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    // Glabā visus dinamiskos dokumenta metadatus vienā JSONB kolonnā
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata;

}