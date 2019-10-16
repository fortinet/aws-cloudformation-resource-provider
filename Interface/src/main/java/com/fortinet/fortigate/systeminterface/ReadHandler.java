package com.fortinet.fortigate.systeminterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;

import com.amazonaws.cloudformation.proxy.AmazonWebServicesClientProxy;
import com.amazonaws.cloudformation.proxy.Logger;
import com.amazonaws.cloudformation.proxy.ProgressEvent;
import com.amazonaws.cloudformation.proxy.OperationStatus;
import com.amazonaws.cloudformation.proxy.ResourceHandlerRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request, final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        } };

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
        HostnameVerifier validHosts = new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);

        String bearerToken = model.getAPIKey();
        String requestUrl = "https://" + model.getFortigateIP() + "/api/v2/cmdb/system/interface/" + model.getName();
        System.out.println(requestUrl);
        HttpResponse response;
        int responseCode;
        if (bearerToken != null && !bearerToken.isEmpty() && requestUrl != null && !requestUrl.isEmpty()) {
            response = sendGetRequest(requestUrl, bearerToken);
            responseCode = response.getStatusLine().getStatusCode();

        } else {
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                    // TODO: change to fail and add test handler.
                    .status(OperationStatus.FAILED).build();
        }
        if (responseCode == 200) {
            try {
                String responseString = new BasicResponseHandler().handleResponse(response);
                JSONObject obj = new JSONObject(responseString);
                JSONArray params = obj.getJSONArray("results");
                JSONObject paramsResults = params.getJSONObject(0);
                model.setType(paramsResults.getString("type"));
                model.setRole(paramsResults.getString("role"));
                model.setVlanid(paramsResults.getInt("vlanid"));
                model.setDistance(paramsResults.getInt("distance"));
                return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
                        .status(OperationStatus.SUCCESS).build();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
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

    public static HttpResponse sendGetRequest(String requestUrl, String BearerToken) {
        try {

            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf).build();

            BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
                    socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager).build();

            HttpGet request = new HttpGet(requestUrl);

            request.setHeader("Authorization", "Bearer" + BearerToken);
            // request.setHeader("Authorization",BearerToken);

            HttpResponse response = httpClient.execute(request);
            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
