package lv.smiltenesnkup.dvs.document.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Pārstāv dokumentu sarakstu (reģistru vai mapi) sistēmā.
 */
@Entity
@Table(name = "document_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

}