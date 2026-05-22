package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.util.MessageUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;

public final class HttpUtil {
   private static final String REQUEST_TYPE_FORM = "application/x-www-form-urlencoded;charset=utf-8";
   private static final String REQUEST_TYPE_JSON = "application/json; charset=utf-8";
   private static final String CHARSET = "utf-8";
   private static Integer CONNECT_TIMEOUT = 10000;
   private static Integer READ_TIMEOUT = 10000;

   private HttpUtil() {
   }

   public static String post(String url, String jsonContent) throws IOException {
      return post(url, jsonContent, null);
   }

   public static String post(String url, String jsonContent, Map<String, String> headers) throws IOException {
      return doRequest("POST", url, jsonContent, "application/json; charset=utf-8", headers);
   }

   public static String post(String url) throws IOException {
      return doRequest("POST", url, "", "application/x-www-form-urlencoded;charset=utf-8", null);
   }

   public static String post(String url, Map<String, String> params) throws IOException {
      return doRequest("POST", url, buildQuery(params), "application/x-www-form-urlencoded;charset=utf-8", null);
   }

   public static String get(String url) throws IOException {
      return doRequest("GET", url, "", "application/x-www-form-urlencoded;charset=utf-8", null);
   }

   public static String get(String url, Map<String, String> params) throws IOException {
      return get(url, params, null);
   }

   public static String get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
      return doRequest("GET", url + buildQuery(params), "", "application/x-www-form-urlencoded;charset=utf-8", headers);
   }

   public static void downloadFile(String urlStr, File saveDir, String fileName) throws IOException {
      URL url = new URL(urlStr);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setConnectTimeout(CONNECT_TIMEOUT);
      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
      InputStream inputStream = conn.getInputStream();
      byte[] getData = readInputStream(inputStream);
      if (!saveDir.exists()) {
         boolean mkdir = saveDir.mkdir();
         MessageUtil.sendConsoleDebugMessage(String.valueOf(mkdir));
      }

      File file = new File(saveDir + File.separator + fileName);
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(getData);
      fos.close();
      inputStream.close();
   }

   public static void setTimeOut(Integer connectTimeout, Integer readTimeout) {
      CONNECT_TIMEOUT = connectTimeout;
      READ_TIMEOUT = readTimeout;
   }

   private static String doRequest(String method, String url, String requestContent, String requestType, Map<String, String> headers) throws IOException {
      HttpURLConnection conn = null;
      OutputStream out = null;

      String rsp;
      try {
         conn = getConnection(new URL(url), method, requestType, headers);
         conn.setConnectTimeout(CONNECT_TIMEOUT);
         conn.setReadTimeout(READ_TIMEOUT);
         if (StrUtil.isNotEmpty(requestContent)) {
            out = conn.getOutputStream();
            out.write(requestContent.getBytes("utf-8"));
         }

         rsp = getResponseAsString(conn);
      } finally {
         if (out != null) {
            out.close();
         }

         if (conn != null) {
            conn.disconnect();
         }
      }

      return rsp;
   }

   private static HttpURLConnection getConnection(URL url, String method, String requestType, Map<String, String> headers) throws IOException {
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod(method);
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,application/json");
      conn.setRequestProperty("Content-Type", requestType);
      if (MapUtil.isNotEmpty(headers)) {
         for (Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
         }
      }

      return conn;
   }

   private static String getResponseAsString(HttpURLConnection conn) throws IOException {
      InputStream es = conn.getErrorStream();
      return es == null ? getStreamAsString(conn.getInputStream(), getResponseCharset(conn)) : getStreamAsString(es, getResponseCharset(conn));
   }

   private static String getStreamAsString(InputStream stream, String charset) throws IOException {
      String var6;
      try {
         Reader reader = new InputStreamReader(stream, charset);
         StringBuilder response = new StringBuilder();
         char[] buff = new char[1024];

         int read;
         while ((read = reader.read(buff)) > 0) {
            response.append(buff, 0, read);
         }

         var6 = response.toString();
      } finally {
         if (stream != null) {
            stream.close();
         }
      }

      return var6;
   }

   private static String buildQuery(Map<String, String> params) throws UnsupportedEncodingException {
      if (params != null && !params.isEmpty()) {
         StringBuilder query = new StringBuilder();
         query.append("?");
         Set<Entry<String, String>> entries = params.entrySet();
         boolean hasParam = false;

         for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (hasParam) {
               query.append("&");
            } else {
               hasParam = true;
            }

            query.append(name).append("=").append(URLEncoder.encode(value, "utf-8"));
         }

         return query.toString();
      } else {
         return "";
      }
   }

   private static byte[] readInputStream(InputStream inputStream) throws IOException {
      ByteArrayOutputStream bos = null;

      byte[] var4;
      try {
         byte[] buffer = new byte[1024];
         bos = new ByteArrayOutputStream();

         int len;
         while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
         }

         var4 = bos.toByteArray();
      } finally {
         if (bos != null) {
            try {
               bos.close();
            } catch (IOException var11) {
               InitApi.PLUGIN.getLogger().log(Level.SEVERE, "readInputStream 发生异常", (Throwable)var11);
            }
         }
      }

      return var4;
   }

   private static String getResponseCharset(HttpURLConnection conn) {
      String contentType = conn.getContentType();
      if (StrUtil.isNotEmpty(contentType)) {
         Matcher matcher = PatternUtil.HTTP_CHARSET.matcher(contentType);
         if (matcher.find()) {
            return matcher.group(1);
         }
      }

      return "utf-8";
   }
}
