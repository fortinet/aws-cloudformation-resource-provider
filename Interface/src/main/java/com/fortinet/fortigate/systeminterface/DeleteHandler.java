package com.fortinet.fortigate.systeminterface;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONObject;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
            }
        }};

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier validHosts = (arg0, arg1) -> true;

        // All hosts will be valid
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
        JSONObject payload = new JSONObject();
        payload.put("vdom", model.getVdom());

        String bearerToken = model.getAPIKey();
        String requestUrl = "https://" + model.getFortigateIP() + "/api/v2/cmdb/system/interface/" + model.getName();
        System.out.println(requestUrl);
        HttpResponse response;
        int responseCode;

        if (bearerToken != null) {
            response = sendDeleteRequest(requestUrl, payload.toString(), bearerToken);
            responseCode = response.getStatusLine().getStatusCode();
        } else {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                //TODO: change to fail and add test handler.
                .status(OperationStatus.FAILED).build();
        }
        if (responseCode == 200) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.SUCCESS).build();
        } else if (responseCode == 400) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else if (responseCode == 403) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else if (responseCode == 404) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else if (responseCode == 405) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else if (responseCode == 413) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else if (responseCode == 429) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else if (responseCode == 500) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        } else {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                .status(OperationStatus.FAILED).build();
        }
    }

    public static HttpResponse sendDeleteRequest(String requestUrl, String payload, String BearerToken) {
        try {
            System.out.println(payload);
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .build();

            BasicHttpClientConnectionManager connectionManager =
                new BasicHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                .setConnectionManager(connectionManager).build();

            HttpDelete request = new HttpDelete(requestUrl);

            request.setHeader("Authorization", "Bearer" + BearerToken);

            System.out.println();

            HttpResponse response = httpClient.execute(request);
            System.out.println("Response:");
            System.out.println("Response Code" + response.getStatusLine().getStatusCode());
            System.out.println(response);
            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}