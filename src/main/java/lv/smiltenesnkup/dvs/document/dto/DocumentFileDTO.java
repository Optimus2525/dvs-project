package lv.smiltenesnkup.dvs.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.smiltenesnkup.dvs.document.enums.FileRole;

import java.time.LocalDateTime;

/**
 * Pārnes dokumenta faila datus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFileDTO {

    private Long id;
    private Long documentCardId;
    private String sharepointFileId;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String uploadedBy;
    private FileRole fileRole;
    private LocalDateTime uploadedAt;

}