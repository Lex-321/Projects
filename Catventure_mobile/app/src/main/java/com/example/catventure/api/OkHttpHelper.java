package com.example.catventure.api;
import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OkHttpHelper {

    public static OkHttpClient createHttpsClient() {
        try {
            // Ustawienie TrustAllCertificates jako TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertificates()};

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // HostnameVerifier ignoruje problemy z nazwą hosta
            HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            // DNS wymuszający użycie adresów IPv4
            Dns ipv4Dns = hostname -> {
                List<InetAddress> allByName = Arrays.asList(InetAddress.getAllByName(hostname));
                List<InetAddress> ipv4Addresses = new ArrayList<>();
                for (InetAddress address : allByName) {
                    if (address instanceof Inet4Address) {
                        ipv4Addresses.add(address);
                    }
                }
                return ipv4Addresses.isEmpty() ? allByName : ipv4Addresses;
            };
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(hostnameVerifier) // Akceptuj każde hostname
                .dns(ipv4Dns)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url();
                        if (url.scheme().equals("http")) {
                            HttpUrl httpsUrl = url.newBuilder().scheme("https").build();
                            request = request.newBuilder().url(httpsUrl).build();
                        }
                        try {
                            return chain.proceed(request); // Próba wykonania żądania
                        } catch (SocketTimeoutException e) {
                            // Logowanie błędów timeout
                            Log.e("OkHttpHelper", "Timeout error: " + e.getMessage());
                            throw e;
                        } catch (IOException e) {
                            // Logowanie innych błędów
                            Log.e("OkHttpHelper", "Network error: " + e.getMessage());
                            throw e;
                        }
                    }
                })
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return new OkHttpClient();
        }
    }

}

