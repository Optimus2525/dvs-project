# Changelog

Visi ievērojamie šī projekta labojumi tiks dokumentēti šajā failā.
Projekts ievēro [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-05-24
### Pievienots
- Pievienots `DocumentWebController` un Thymeleaf bāzes izkārtojums (`base.html`).
- Izveidoti atkārtoti izmantojami Thymeleaf UI komponenti formām (`components/forms.html`).
- Realizēta dinamiska HTML formas un tabulas ģenerēšana no `JSONB` metadatiem (`dashboard.html`).
- Pievienota datumu formatēšana (Latvijas standarts `dd.MM.yyyy`) gan ievades laukos (Flatpickr), gan datu tabulā, saglabājot ISO standartu datubāzē.
- Pievienoti V2 un V3 Flyway migrācijas skripti (shēmas sinhronizācija un sēklas dati).
- Pievienota pagaidu `SecurityConfig`, atslēdzot CSRF, un atspējota OAuth2 auto-konfigurācija izstrādes vajadzībām.
- Sistēmas ports nomainīts uz `8095` un versija definēta `application.yaml` failā.

## [0.1.0] - 2026-05-24
### Pievienots
- Sākotnējā Spring Boot 4.0.6 (Java 21) projekta bāze.
- PostgreSQL infrastruktūras konfigurācija (Docker Compose saderība).
- Flyway migrācijas skripts (`V1__init_schema.sql`) Dinamiskā Metadatu Dzinēja tabulām.
- Entītijas (`DocumentList`, `FieldDefinition`, `DocumentCard`) ar Hibernate 6 `JSONB` datu tipa atbalstu.
- DTO slānis ar latvisku Jakarta Validation un MapStruct maperiem.
- `DocumentListService` un `DocumentCardService` biznesa loģika.
- Native PostgreSQL JSONB meklēšanas vaicājums (`@>`) dokumentu atlasīšanai pēc dinamiskajiem laukiem.