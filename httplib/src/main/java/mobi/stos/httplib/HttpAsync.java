package mobi.stos.httplib;

import android.os.AsyncTask;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import mobi.stos.httplib.enumm.Method;
import mobi.stos.httplib.inter.FutureCallback;
import mobi.stos.httplib.inter.RestComposer;
import mobi.stos.httplib.task.CustomHttpTask;
import mobi.stos.httplib.util.Logger;

/**
 * Created by Weibson on 04/10/2017.
 */

public class HttpAsync implements RestComposer {

    private boolean debug = false;
    private boolean aceitarCertificadoInvalido = false;
    private boolean execucaoSerial = true;

    private final URL url;
    private final Map<String, Object> params;
    private final Map<String, String> headers;

    public HttpAsync(URL url) {
        this.url = url;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public HttpAsync addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpAsync addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public HttpAsync addParam(String key, String attr, Object value) {
        Map<String, Object> p = (Map<String, Object>) params.get(key);
        if (p == null) {
            p = new HashMap<>();
        }
        p.put(attr, value);
        params.put(key, p);
        return this;
    }

    public HttpAsync setDebug(boolean debug) {
        Logger.debug = debug;
        this.debug = debug;
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

    private void execute(final Method method, final FutureCallback callback) {
        CustomHttpTask task = new CustomHttpTask(this.url);
        task.setMethod(method);
        task.addCustomHeader(this.headers);
        task.setParams(this.params);
        task.setCallback(callback);
        task.setDebug(this.debug);
        task.setTrustAllCerts(this.aceitarCertificadoInvalido);
        task.executeOnExecutor(execucaoSerial ? AsyncTask.SERIAL_EXECUTOR : AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void get(FutureCallback callback) {
        Logger.d("Executando método get");
        this.execute(Method.GET, callback);
    }

    @Override
    public void post(FutureCallback callback) {
        Logger.d("Executando método post");
        this.execute(Method.POST, callback);
    }

    @Override
    public void put(FutureCallback callback) {
        Logger.d("Executando método put");
        this.execute(Method.PUT, callback);
    }

    @Override
    public void delete(FutureCallback callback) {
        Logger.d("Executando método delete");
        this.execute(Method.DELETE, callback);
    }
}
