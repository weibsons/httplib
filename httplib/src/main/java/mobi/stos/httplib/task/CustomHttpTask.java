package mobi.stos.httplib.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mobi.stos.httplib.enumm.Method;
import mobi.stos.httplib.inter.FutureCallback;
import mobi.stos.httplib.util.Logger;

/**
 * Created by Weibson on 04/10/2017.
 */

public class CustomHttpTask extends AsyncTask<Void, Void, Object> {

    private final URL url;
    private FutureCallback callback;
    private Method method;
    private boolean trustAllCerts;

    private Map<String, Object> params;
    private Map<String, String> headers;

    private int statusCode;
    private Exception exception = null;

    public void setTrustAllCerts(boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }

    public CustomHttpTask(URL url) {
        this.url = url;
    }

    public void addCustomHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setCallback(FutureCallback callback) {
        this.callback = callback;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((arg0, arg1) -> true);
        } catch (Exception e) {
        }
    }

    @Override
    protected Object doInBackground(Void... voids) {
        if (trustAllCerts) {
            trustAllCertificates();
        }

        Object object = null;
        HttpURLConnection connection = null;
        try {
            Logger.d("Estabelecimento conexão com o endpoint...");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(String.valueOf(method));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setUseCaches(false);
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> map : headers.entrySet()) {
                    connection.setRequestProperty(map.getKey(), map.getValue());
                }
            }

            if (params != null && !params.isEmpty()) {
                JSONObject json = new JSONObject();
                for (Map.Entry<String, Object> map : params.entrySet()) {
                    if (map.getValue() instanceof Map) {
                        JSONObject iJson = new JSONObject();
                        Map<String, Object> innerMap = (Map<String, Object>) map.getValue();
                        for (Map.Entry<String, Object> inner : innerMap.entrySet()) {
                            iJson.put(inner.getKey(), String.valueOf(inner.getValue()));
                        }
                        json.put(map.getKey(), iJson);
                    } else if (map.getValue() instanceof JSONObject) {
                        json.put(map.getKey(), map.getValue());
                    } else if (map.getValue() instanceof JSONArray) {
                        json.put(map.getKey(), map.getValue());
                    } else {
                        json.put(map.getKey(), String.valueOf(map.getValue()));
                    }
                }
                Logger.d(json.toString());

                byte[] bytes = json.toString().getBytes("UTF-8");
                connection.setRequestProperty("Content-Length", Integer.toString(bytes.length));

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(bytes, 0, bytes.length);
                wr.flush();
                wr.close();
            }

            statusCode = connection.getResponseCode();
            Logger.d("Status Code: " + statusCode);

            String contentEncoding = connection.getHeaderField("Content-Encoding");
            Logger.d("Response content encoding: " + contentEncoding);

            InputStream inputStream = null;
            try {
                if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                    if (statusCode < 400) {
                        inputStream = new GZIPInputStream(connection.getInputStream());
                    } else {
                        inputStream = new GZIPInputStream(connection.getErrorStream());
                    }
                } else {
                    if (statusCode < 400) {
                        inputStream = connection.getInputStream();
                    } else {
                        inputStream = connection.getErrorStream();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String jsonString = null;
            if (inputStream != null) {
                jsonString = inputStreamToString(inputStream);
            }

            if (!TextUtils.isEmpty(jsonString)) {
                Object preJson = new JSONTokener(jsonString).nextValue();
                if (preJson instanceof JSONObject) {
                    object = new JSONObject(jsonString);
                } else if (preJson instanceof JSONArray) {
                    object = new JSONArray(jsonString);
                } else {
                    object = jsonString;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return object;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Logger.d("onPreExecute");
        if (callback != null) {
            callback.onBeforeExecute();
        }
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        Logger.d("onPostExecute");

        if (callback != null) {
            Logger.d("Processando retorno");
            if (exception != null) {
                exception.printStackTrace();
                Logger.d("Exceção gerada na conclusão do processo. Erro: " + exception.getMessage(), exception.getCause());
                callback.onFailure(exception);
            } else {
                Logger.d("Retornando dados com sucesso. Status Code: " + statusCode);
                callback.onSuccess(statusCode, object);
            }
            callback.onAfterExecute();
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
