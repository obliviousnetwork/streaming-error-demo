package com.example.demo;

import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.security.cert.CertificateException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Example demonstrating HTTP/2 streaming using OkHttp. This class sends
 * streamed data to a
 * specified endpoint using HTTP/2.
 */
public class Http2StreamingExample {

    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final OkHttpClient httpClient;

    public Http2StreamingExample() {
        // Configure the HTTP client to support both HTTP/2 and HTTP/1.1
        this.httpClient = getUnsafeOkHttpClient()
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();
    }

    /**
     * Entry point to simulation
     *
     * @apiNote : init the http2 streaming example class and start streaming
     */
    public static void main(String url) {
        Http2StreamingExample example = new Http2StreamingExample();
        try {
            example.executeStreamingWorkflow(url);
        } catch (IOException e) {
            System.out.println("Error in executeStreamingWorkflow: " + e.getMessage());
        }
    }

    /**
     * @throws IOException if an I/O error occurs
     * @apiNote : Start streamed data to the OBN endpoint.
     */
    public void executeStreamingWorkflow(String url) throws IOException {

        // create streaming request and setup a network call
        CustomRequestBody requestBody = initStreaming(url);

        // Example key configuration data (byte array)
        byte[] keyConfigBytes = new byte[] {
                10, -117, 1, 10, 64, 101, 99, 48, 99, 98, 56, 56, 52, 51, 51, 50,
                53, 98, 53, 100, 50, 52, 54, 98, 101, 52, 55, 48, 99, 54, 50, 50,
                54, 57, 102, 100, 50, 49, 54, 97, 56, 53
        };

        // try (FileOutputStream fos = new FileOutputStream("bytes.bin")) {
        // fos.write(keyConfigBytes);
        // }
        requestBody.setData(keyConfigBytes);
    }

    /**
     * @apiNote : Create a full duplex request with provided URL (@DIRECT_URL
     *          / @OBN_URL)
     *          <p>
     *          Setup full duplex request with client and attach a callback as
     *          listener of data received
     */
    private CustomRequestBody initStreaming(String url) {
        CustomRequestBody requestBody = new CustomRequestBody();
        Request request = new Request.Builder()
                .url(url) // <- ## set url for request
                .post(requestBody)
                .build();

        System.out.println("starting call to " + url);
        // set the request to api client
        httpClient.newCall(request)
                // set-up the client callback <- ## attach response listener
                .enqueue(new ClientCallback());
        return requestBody;
    }

    /**
     * @apiNote : Client listener callback to keep a note of data/error recieved
     */
    private static class ClientCallback implements Callback {

        /**
         * @apiNote : Called when the request could not be executed due to cancellation,
         *          a connectivity
         *          problem or timeout.
         */
        @Override
        public void onFailure(Call call, IOException e) {
            System.out.println("onFailure callback");
            e.printStackTrace();
        }

        /**
         * @apiNote : called when client get response (data/error) from server
         */
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try (ResponseBody responseBody = response.body()) {
                System.out.println("Response status code: " + response.code());
                System.out.println("start printing response body");
                System.out.println("Response body: " + responseBody);
                System.out.print("> ");
                Reader in = responseBody.charStream();
                PrintStream out = System.out;

                try {
                    int c;

                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }

                System.out.println("finished printing response body");
            }
        }
    }
}
