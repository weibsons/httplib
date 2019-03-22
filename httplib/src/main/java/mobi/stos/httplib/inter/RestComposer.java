package mobi.stos.httplib.inter;

import mobi.stos.httplib.enumm.Method;

/**
 * Created by Weibson on 04/10/2017.
 */

public interface RestComposer {

    void get(FutureCallback callback);

    void post(FutureCallback callback);

    void put(FutureCallback callback);

    void delete(FutureCallback callback);

    void execute(Method method);

}
