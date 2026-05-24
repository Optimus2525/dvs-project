package lv.smiltenesnkup.dvs.document.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Definē dinamiskos laukus, kas piesaistīti konkrētam dokumentu sarakstam.
 */
@Entity
@Table(name = "field_definition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_list_id", nullable = false)
    private DocumentList documentList;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // e.g., TEXT, DATE, SELECT

    // Glabā papildus konfigurāciju JSONB formātā (piem., dropdown izvēlnes opcijas)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> options;

}