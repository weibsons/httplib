package mobi.stos.httplib.inter;

/**
 * Created by Weibson on 04/10/2017.
 */

public interface FutureCallback {

    void onBeforeExecute();

    void onAfterExecute();

    /**
     * Retorna sempre que houver sucesso de conexão com o servidor.
     * Mesmo se o servidor retornar HTTP Status acima de 400 entrará nesse local, a menos que haja
     * um erro na biblioteca ou requisição.
     *
     * @param responseCode int
     * @param object Object
     */
    void onSuccess(int responseCode, Object object);

    void onFailure(Exception exception);

}
