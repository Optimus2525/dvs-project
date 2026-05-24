# Changelog

Visi ievērojamie šī projekta labojumi tiks dokumentēti šajā failā.
Projekts ievēro [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-05-24
### Pievienots
- Sākotnējā Spring Boot 4.0.6 (Java 21) projekta bāze.
- PostgreSQL infrastruktūras konfigurācija (Docker Compose saderība).
- Flyway migrācijas skripts (`V1__init_schema.sql`) Dinamiskā Metadatu Dzinēja tabulām.
- Entītijas (`DocumentList`, `FieldDefinition`, `DocumentCard`) ar Hibernate 6 `JSONB` datu tipa atbalstu.
- DTO slānis ar latvisku Jakarta Validation un MapStruct maperiem.
- `DocumentListService` un `DocumentCardService` biznesa loģika.
- Native PostgreSQL JSONB meklēšanas vaicājums (`@>`) dokumentu atlasīšanai pēc dinamiskajiem laukiem.