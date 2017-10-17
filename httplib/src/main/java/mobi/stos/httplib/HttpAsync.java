package mobi.stos.httplib;

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

    private final URL url;
    private final Map<String, Object> params;

    public HttpAsync(URL url) {
        this.url = url;
        this.params = new HashMap<>();
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

    private void execute(final Method method, final FutureCallback callback) {
        CustomHttpTask task = new CustomHttpTask(this.url);
        task.setMethod(method);
        task.setParams(this.params);
        task.setCallback(callback);
        task.setDebug(this.debug);
        task.execute();
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
