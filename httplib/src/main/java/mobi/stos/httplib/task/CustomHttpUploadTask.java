package mobi.stos.httplib.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import mobi.stos.httplib.bean.FormData;
import mobi.stos.httplib.bean.UploadData;
import mobi.stos.httplib.inter.FutureCallback;
import mobi.stos.httplib.inter.PreCallback;
import mobi.stos.httplib.inter.SimpleCallback;
import mobi.stos.httplib.util.Logger;
import mobi.stos.httplib.util.TrustAllCertificates;

public class CustomHttpUploadTask extends AsyncTask<Void, Void, Object> {

    private final URL url;
    private FutureCallback callback;

    private PreCallback onPreExecuteCallback;
    private SimpleCallback onSuccessCallback;
    private SimpleCallback onFailureCallback;

    private boolean trustAllCerts;

    private Map<String, String> headers;
    private UploadData uploadData;
    private List<FormData> formDataList;

    private int statusCode;
    private Exception exception = null;

    // region setters
    public void setTrustAllCerts(boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }

    public CustomHttpUploadTask(URL url) {
        this.url = url;
    }

    public void addCustomHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setCallback(FutureCallback callback) {
        this.callback = callback;
    }

    public void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    public void setFormDataList(List<FormData> formDataList) {
        this.formDataList = formDataList;
    }

    public void setUploadData(UploadData uploadData) {
        this.uploadData = uploadData;
    }

    public void setOnPreExecuteCallback(PreCallback onPreExecuteCallback) {
        this.onPreExecuteCallback = onPreExecuteCallback;
    }

    public void setOnSuccessCallback(SimpleCallback onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
    }

    public void setOnFailureCallback(SimpleCallback onFailureCallback) {
        this.onFailureCallback = onFailureCallback;
    }
    // endregion

    @Override
    protected Object doInBackground(Void... voids) {
        if (trustAllCerts) {
            TrustAllCertificates.blindTrust();
        }

        Object object = null;
        HttpURLConnection connection = null;
        try {
            Logger.d("Estabelecimento conexão com o endpoint...");
            Logger.d("Endpoint: " + url.toString());

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> map : headers.entrySet()) {
                    connection.setRequestProperty(map.getKey(), map.getValue());
                }
            }
            connection.setRequestProperty(uploadData.getKey(), uploadData.getFileUri());

            // region DataOutputStream
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\""+ uploadData.getKey() +"\";filename=\""+ uploadData.getFileUri() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            FileInputStream fileInputStream = new FileInputStream(uploadData.getFile());
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);

            if (formDataList != null && !formDataList.isEmpty()) {
                for (FormData formData : formDataList) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + formData.getName() + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(formData.getValue());
                    dos.writeBytes(lineEnd);
                }
            }
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fileInputStream.close();
            dos.flush();
            dos.close();
            // endregion

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
        if (onPreExecuteCallback != null) {
            onPreExecuteCallback.onCallback();
        }
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        Logger.d("onPostExecute");

        if (callback != null || onSuccessCallback != null || onFailureCallback != null) {
            Logger.d("Processando retorno");
            if (exception != null) {
                exception.printStackTrace();
                Logger.d("Exceção gerada na conclusão do processo. Erro: " + exception.getMessage(), exception.getCause());
                if (callback != null) {
                    callback.onFailure(exception);
                }
                if (onFailureCallback != null) {
                    onFailureCallback.onCallback(exception);
                }
            } else {
                Logger.d("Retornando dados com sucesso. Status Code: " + statusCode);
                if (callback != null) {
                    callback.onSuccess(statusCode, object);
                }
                if (onSuccessCallback != null) {
                    onSuccessCallback.onCallback(statusCode, object);
                }
            }
            if (callback != null) {
                callback.onAfterExecute();
            }
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
