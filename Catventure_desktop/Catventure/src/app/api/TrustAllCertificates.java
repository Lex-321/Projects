package app.api;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

class TrustAllCertificates implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return null; // Nie weryfikujemy żadnych certyfikatów
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType) {
        // Brak weryfikacji klienta
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType) {
        // Brak weryfikacji serwera
    }
}
