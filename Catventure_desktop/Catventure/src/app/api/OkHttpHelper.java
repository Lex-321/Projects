package app.api;

import okhttp3.OkHttpClient;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class OkHttpHelper {
    public static OkHttpClient createHttpsClient() {
        try {
            // Tworzenie zaufanego menedżera certyfikatów (zaufanie do wszystkich certyfikatów)
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            // Inicjalizacja SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
            // Tworzenie klienta OkHttp z odpowiednią konfiguracją SSL
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCertificates[0]);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error creating HTTPS client", e);
        }
    }
}

