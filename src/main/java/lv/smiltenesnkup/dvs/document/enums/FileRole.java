package lv.smiltenesnkup.dvs.document.enums;

/**
 * Definē faila lomu dokumenta kartītē, kas ir kritiski svarīgi eParaksts integrācijai.
 */
public enum FileRole {
    MAIN_DOCUMENT,      // Pamatdokuments (piemēram, Word, PDF)
    ATTACHMENT,         // Pielikums (piemēram, Excel, JPG)
    SCAN,               // Skenēta kopija
    SIGNED_CONTAINER    // Parakstīts fails (.edoc, .asice)

}