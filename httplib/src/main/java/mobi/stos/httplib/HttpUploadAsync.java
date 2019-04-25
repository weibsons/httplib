package mobi.stos.httplib;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.stos.httplib.bean.FormData;
import mobi.stos.httplib.bean.UploadData;
import mobi.stos.httplib.enumm.Method;
import mobi.stos.httplib.inter.FutureCallback;
import mobi.stos.httplib.inter.PreCallback;
import mobi.stos.httplib.inter.RestComposer;
import mobi.stos.httplib.inter.SimpleCallback;
import mobi.stos.httplib.task.CustomHttpTask;
import mobi.stos.httplib.task.CustomHttpUploadTask;
import mobi.stos.httplib.util.Logger;

/**
 * Created by Weibson on 04/10/2017.
 */

public class HttpUploadAsync {

    private PreCallback onPreExecuteCallback;
    private SimpleCallback onSuccessCallback;
    private SimpleCallback onFailureCallback;

    private boolean debug = false;
    private boolean aceitarCertificadoInvalido = false;
    private boolean execucaoSerial = true;

    private final URL url;
    private UploadData uploadData;
    private final List<FormData> params;
    private final Map<String, String> headers;

    public HttpUploadAsync(URL url) {
        this.url = url;
        this.params = new ArrayList<>();
        this.headers = new HashMap<>();
    }

    public HttpUploadAsync addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpUploadAsync addParam(String key, String value) {
        params.add(new FormData(key, value));
        return this;
    }

    public HttpUploadAsync setDebug(boolean debug) {
        Logger.debug = debug;
        this.debug = debug;
        return this;
    }

    public void addUploadData(File file, String fileUri, String fileContentType, String key) {
        this.uploadData = new UploadData(file, fileUri, fileContentType, key);
    }

    /**
     * Sem retorno.
     * @param callback PreCallback
     * @return HttpAsync
     */
    public HttpUploadAsync addOnPreExecuteCallback(PreCallback callback) {
        this.onPreExecuteCallback = callback;
        return this;
    }

    /**
     * Ordem dos retornos do callback
     * INDEX 0 = Status Code (Integer)
     * INDEX 1 = JSONObject, JSONArray, String, Object, etc.
     * @param callback SimpleCallback
     * @return HttpAsync
     */
    public HttpUploadAsync addOnSuccessCallback(SimpleCallback callback) {
        this.onSuccessCallback = callback;
        return this;
    }

    public HttpUploadAsync addOnFailureCallback(SimpleCallback callback) {
        this.onFailureCallback = callback;
        return this;
    }

    /**
     * <p>A execução serial será por padrão TRUE.</p>
     * <p>Significa que existirá somente uma tarefa rodando em forma serial. (Uma tarefa acaba para
     * executar a próxima tarefa).</p>
     * <p>Se essa opção for marcado como FALSE a execução acontecerá de forma não serial, ou seja,
     * paralelas.</p>
     *
     * <p>
     * <strong>Aviso:</strong><br>
     * Permitir que várias tarefas sejam executadas em paralelo a partir de um conjunto de
     * encadeamentos geralmente não é o que se deseja, porque a ordem de sua operação não está
     * definida. <br><br>
     *
     * Por exemplo, se essas tarefas forem usadas para modificar qualquer estado em comum (como
     * gravar um arquivo devido a um clique no botão), não há garantias sobre a ordem das
     * modificações. Sem um trabalho cuidadoso, é possível, em casos raros, que a versão mais
     * recente dos dados seja sobreposta por uma mais antiga, levando a problemas de perda de dados
     * e estabilidade. Tais mudanças são melhor executadas em serial; Para garantir que esse
     * trabalho seja serializado, independentemente da versão da plataforma, você pode usar essa
     * função com TRUE.
     * </p>
     *
     * @param execucaoSerial Boolean
     */
    public void setExecucaoSerial(boolean execucaoSerial) {
        this.execucaoSerial = execucaoSerial;
    }

    public void setAceitarCertificadoInvalido(boolean aceitarCertificadoInvalido) {
        this.aceitarCertificadoInvalido = aceitarCertificadoInvalido;
    }

    public void upload() {
        this.upload(null);
    }

    public void upload(final FutureCallback callback) {
        CustomHttpUploadTask task = new CustomHttpUploadTask(this.url);
        task.addCustomHeader(this.headers);
        task.setFormDataList(this.params);
        task.setUploadData(this.uploadData);
        task.setCallback(callback);
        task.setOnPreExecuteCallback(this.onPreExecuteCallback);
        task.setOnSuccessCallback(this.onSuccessCallback);
        task.setOnFailureCallback(this.onFailureCallback);
        task.setDebug(this.debug);
        task.setTrustAllCerts(this.aceitarCertificadoInvalido);
        task.executeOnExecutor(execucaoSerial ? AsyncTask.SERIAL_EXECUTOR : AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
