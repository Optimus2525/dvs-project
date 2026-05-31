package lv.smiltenesnkup.dvs.document.model;

import jakarta.persistence.*;
import lombok.*;
import lv.smiltenesnkup.dvs.document.enums.FileRole;

import java.time.LocalDateTime;

/**
 * Pārstāv fizisku failu (vai saiti uz SharePoint), kas piesaistīts dokumenta kartītei.
 */
@Entity
@Table(name = "document_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_card_id", nullable = false)
    private DocumentCard documentCard;

    @Column(name = "sharepoint_file_id")
    private String sharepointFileId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "uploaded_by", nullable = false)
    private String uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_role", nullable = false)
    private FileRole fileRole = FileRole.ATTACHMENT;

    @Column(name = "uploaded_at", insertable = false, updatable = false)
    private LocalDateTime uploadedAt;

}