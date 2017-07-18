package com.jihf.facedetect.youtu;

import com.jihf.facedetect.youtu.sign.YoutuSign;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author tyronetao
 */
public class Youtu {

  public static class TrustAnyTrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[] {};
    }
  }

  private static class TrustAnyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }

  // 30 days
  private static int EXPIRED_SECONDS = 2592000;
  private String m_appid;
  private String m_secret_id;
  private String m_secret_key;
  private String m_end_point;
  private boolean m_use_https;

  /**
   * Youtu 构造方法
   *
   * @param appid 授权appid
   * @param secret_id 授权secret_id
   * @param secret_key 授权secret_key
   */
  public Youtu(String appid, String secret_id, String secret_key, String end_point) {
    m_appid = appid;
    m_secret_id = secret_id;
    m_secret_key = secret_key;
    m_end_point = end_point;
    m_use_https = end_point.startsWith("https");
  }

  public String StatusText(int status) {

    String statusText = "UNKOWN";

    switch (status) {
      case 0:
        statusText = "CONNECT_FAIL";
        break;
      case 200:
        statusText = "HTTP OK";
        break;
      case 400:
        statusText = "BAD_REQUEST";
        break;
      case 401:
        statusText = "UNAUTHORIZED";
        break;
      case 403:
        statusText = "FORBIDDEN";
        break;
      case 404:
        statusText = "NOTFOUND";
        break;
      case 411:
        statusText = "REQ_NOLENGTH";
        break;
      case 423:
        statusText = "SERVER_NOTFOUND";
        break;
      case 424:
        statusText = "METHOD_NOTFOUND";
        break;
      case 425:
        statusText = "REQUEST_OVERFLOW";
        break;
      case 500:
        statusText = "INTERNAL_SERVER_ERROR";
        break;
      case 503:
        statusText = "SERVICE_UNAVAILABLE";
        break;
      case 504:
        statusText = "GATEWAY_TIME_OUT";
        break;
    }
    return statusText;
  }

  private String SendHttpRequest(JSONObject postData, String mothod)
      throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {

    StringBuffer mySign = new StringBuffer("");
    YoutuSign.appSign(m_appid, m_secret_id, m_secret_key, System.currentTimeMillis() / 1000 + EXPIRED_SECONDS, "",
        mySign);

    System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
    System.setProperty("sun.net.client.defaultReadTimeout", "30000");
    URL url = new URL(m_end_point + mothod);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // set header
    connection.setRequestMethod("POST");
    connection.setRequestProperty("accept", "*/*");
    //		connection.setRequestProperty("Host", "api.youtu.qq.com");
    connection.setRequestProperty("user-agent", "youtu-android-sdk");
    connection.setRequestProperty("Authorization", mySign.toString());

    //		connection.setConnectTimeout(30000);
    //		connection.setReadTimeout(30000);
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setUseCaches(false);
    connection.setInstanceFollowRedirects(true);
    connection.setRequestProperty("Content-Type", "text/json");
    connection.connect();

    // POST请求
    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

    postData.put("app_id", m_appid);
    out.write(postData.toString().getBytes("utf-8"));
    //out.writeBytes(postData.toString());
    out.flush();
    out.close();
    // 读取响应
    InputStream isss = connection.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(isss));
    String lines;
    StringBuffer resposeBuffer = new StringBuffer("");
    while ((lines = reader.readLine()) != null) {
      lines = new String(lines.getBytes(), "utf-8");
      resposeBuffer.append(lines);
    }
    reader.close();
    // 断开连接
    connection.disconnect();
    return resposeBuffer.toString();
  }

  private String SendHttpsRequest(JSONObject postData, String mothod)
      throws NoSuchAlgorithmException, KeyManagementException, IOException, JSONException {
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

    StringBuffer mySign = new StringBuffer("");
    YoutuSign.appSign(m_appid, m_secret_id, m_secret_key, System.currentTimeMillis() / 1000 + EXPIRED_SECONDS, "",
        mySign);

    System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
    System.setProperty("sun.net.client.defaultReadTimeout", "30000");

    URL url = new URL(m_end_point + mothod);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setSSLSocketFactory(sc.getSocketFactory());
    connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
    // set header
    connection.setRequestMethod("POST");
    connection.setRequestProperty("accept", "*/*");
    //		connection.setRequestProperty("Host", "youtu.api.qcloud.com");
    connection.setRequestProperty("user-agent", "youtu-android-sdk");
    connection.setRequestProperty("Authorization", mySign.toString());

    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setUseCaches(false);
    connection.setInstanceFollowRedirects(true);
    connection.setRequestProperty("Content-Type", "text/json");
    connection.connect();

    OutputStream outaa = connection.getOutputStream();
    // POST请求
    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

    postData.put("app_id", m_appid);
    out.write(postData.toString().getBytes("utf-8"));
    // 刷新、关闭
    out.flush();
    out.close();

    // 读取响应
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String lines;
    StringBuffer resposeBuffer = new StringBuffer("");
    while ((lines = reader.readLine()) != null) {
      lines = new String(lines.getBytes(), "utf-8");
      resposeBuffer.append(lines);
    }
    reader.close();
    // 断开连接
    connection.disconnect();

    return resposeBuffer.toString();
  }

  public String SendRequest(JSONObject postData, String method)
      throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {
    return m_use_https ? SendHttpsRequest(postData, method) : SendHttpRequest(postData, method);
  }
}
