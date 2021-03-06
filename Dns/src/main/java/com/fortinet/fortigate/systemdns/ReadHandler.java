package com.fortinet.fortigate.systemdns;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
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

        //TODO: implment better way to handle JSON

        String bearerToken = model.getAPIKey();
        String requestUrl = "https://" + model.getFortigateIP() + "/api/v2/cmdb/system/dns";
        System.out.println(requestUrl);
        HttpResponse response;
        int responseCode ;
        if(bearerToken != null && !bearerToken.isEmpty() && requestUrl != null && !requestUrl.isEmpty() ){
            response = sendGetRequest(requestUrl, bearerToken);
            responseCode = response.getStatusLine().getStatusCode();
        }
        else{
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            //TODO: change to fail and add test handler.
            .status(OperationStatus.FAILED).build();
        }
        if (responseCode == 200){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.SUCCESS).build();
        }
        else if (responseCode == 400){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else if (responseCode == 403){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else if (responseCode == 404){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else if (responseCode == 405){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else if (responseCode == 413){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else if (responseCode == 429){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else if (responseCode == 500){
            return ProgressEvent.<ResourceModel, CallbackContext>builder().resourceModel(model)
            .status(OperationStatus.FAILED).build();
        }
        else{
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

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
              RegistryBuilder.<ConnectionSocketFactory> create()
              .register("https", sslsf)
              .build();

            BasicHttpClientConnectionManager connectionManager =
              new BasicHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
              .setConnectionManager(connectionManager).build();


            HttpGet request = new HttpGet(requestUrl);

            request.setHeader("Authorization" ,"Bearer" + BearerToken);
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