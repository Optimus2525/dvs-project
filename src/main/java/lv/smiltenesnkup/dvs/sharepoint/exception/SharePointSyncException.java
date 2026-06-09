package lv.smiltenesnkup.dvs.sharepoint.exception;

/**
 * Tiek izmests, ja rodas kļūda saziņā ar Microsoft Graph API vai datu sinhronizācijā.
 */
public class SharePointSyncException extends RuntimeException {

    public SharePointSyncException(String message) {
        super(message);
    }

    public SharePointSyncException(String message, Throwable cause) {
        super(message, cause);
    }

}