# Changelog

Visi ievērojamie šī projekta labojumi tiks dokumentēti šajā failā.
Projekts ievēro [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4.0] - 2026-05-30
### Pievienots
- **Administratora Panelis:** Izveidots centralizēts administratora panelis (`/admin/dashboard`).
- **Spring Security:** Ieviesta pagaidu *In-Memory* autentifikācija un lomu bāzēta piekļuves kontrole (RBAC) administratora maršrutu aizsardzībai.
- **Thymeleaf Security:** Integrēta sānjoslas un navigācijas elementu dinamiska slēpšana atkarībā no lietotāja lomas.
- **CSS Redaktors:** Ieviests UI iestatījumu redaktors (`/admin/settings`), kas ļauj administratoram dinamiski mainīt sistēmas vizuālos parametrus.
- **Formu stili:** Pievienoti jauni CSS mainīgie (`--dvs-input-font-size`, `--dvs-input-padding`) ievades lauku formātam.

### Labots
- Novērsta Thymeleaf SpEL parsēšanas kļūda CSS blokā, ieviešot drošas `null` pārbaudes, kas ļauj ielādēt pieteikšanās un kļūdu lapas bez `GlobalControllerAdvice` iejaukšanās.

## [1.3.0] - 2026-05-29
### Pievienots
- **Uzdevumu Rediģēšana:** Ieviests modālais logs uzdevumu detalizētai apskatei un rediģēšanai ar RBAC (izveidotāja un izpildītāja tiesību nošķiršanu).
- **Izveidotāja lauks:** Pievienots `createdBy` lauks `Task` entītijai (Flyway V7) un tas atspoguļots lietotāja saskarnes tabulā.
- **Darbplūsmas UX:** Ieviesta dinamiska atgriešanas iemesla pieprasīšana, automātiski aktivizējot iepriekšējā apakšuzdevuma apraksta lauku.
- **Paziņojumi:** Uzlabots Toast paziņojumu mehānisms, integrējot pogu "Esmu iepazinies" un izmantojot Bootstrap JS API.

### Labots
- Novērsta "Race Condition" kļūda datubāzē, pārveidojot apakšuzdevumu saglabāšanu no paralēlas uz secīgu izpildi.
- Novērsta `@NotNull` validācijas kļūda, ieviešot `TaskUpdateDTO` galvenā uzdevuma daļējai atjaunināšanai (Partial Update).
- Pievienota `@OrderBy` anotācija `Task` entītijai, nodrošinot stingru apakšuzdevumu secību.
- Pārlūka `alert()` un `prompt()` logi aizstāti ar vizuāliem Bootstrap Alerts blokiem.

## [1.2.0] - 2026-05-26
### Pievienots (Added)
- Pilna biznesa loģika (State Machine) Kompleksajiem (Secīgiem un Paralēliem) uzdevumiem.
- Dinamiska formu ģenerēšana UI, atkarībā no izvēlētā uzdevuma tipa.
- Apakšuzdevumu statusu pārvaldība tieši no "Mani Uzdevumi" tabulas ar automātisku Workflow virzību.
- Popover (Toast) paziņojumu sistēmas pamati (Backend API un JS polling).
- Iespēja mainīt testa lietotāju UI, lai pārbaudītu darbplūsmu starp dažādiem izpildītājiem.

### Izlabots (Fixed)
- Novērsta Jackson JSON parsēšanas kļūda, nomainot `boolean active` uz `Boolean active` iekš `SubTaskDTO`.
- Novērsta Hibernate kļūda, pievienojot `@Builder.Default` un manuālu `taskType` kartēšanu `TaskServiceImpl` parastajiem uzdevumiem.
- Salabota JS lietotāju meklēšanas (Autocomplete) un sekotāju pievienošanas funkcionalitāte.
- Datumu sūtīšana uz Backend standartizēta uz ISO formātu (yyyy-MM-dd), izmantojot JS palīgfunkciju.

## [1.1.0] - 2026-05-25
### Pievienots (Added)
- Globāla `GlobalControllerAdvice` konfigurācija dinamiskai aplikācijas versijas atspoguļošanai Thymeleaf Footer sadaļā.
- Administratora skats (`/admin/lists`) un API jaunu dokumentu sarakstu un to dinamisko lauku izveidei vienā tranzakcijā.
- Jauni dinamisko lauku tipi formu ģeneratorā: Checkbox, Textarea, File.
- Uzdevumu (Workflow) moduļa pamati: `Task` un `SubTask` entītijas, kā arī `V4` Flyway migrācija.
- "Mani uzdevumi" panelis ar cilnēm "Mani uzdevumi" (pēc atbildīgās personas) un "Sekoju līdzi" (pēc JSONB sekotāju masīva).
- Uzdevumu izveides modālais logs ar datumu validāciju (Flatpickr), dinamisku AJAX lietotāju meklēšanu un apakšuzdevumu pievienošanu (limitēts līdz 3).
- Dinamiska termiņu aprēķināšana un vizuāla attēlošana tabulā (krāsošana sarkanā/zaļā krāsā atkarībā no atlikušajām dienām).
- Native PostgreSQL vaicājums JSONB datu atlasīšanai, izmantojot `@>` un `jsonb_build_array` funkciju.

### Izlabots (Fixed)
- Novērsta MapStruct un Hibernate relāciju kartēšanas kļūda (`TransientPropertyValueException`), pievienojot pielāgotu `mapDocumentCard` metodi gadījumiem, kad uzdevums tiek veidots bez piesaistes dokumentam.

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