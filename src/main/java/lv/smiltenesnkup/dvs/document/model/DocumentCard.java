package lv.smiltenesnkup.dvs.document.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "sharepoint_file_id")
    private String sharepointFileId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    // Glabā visus dinamiskos dokumenta metadatus vienā JSONB kolonnā
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata;

}