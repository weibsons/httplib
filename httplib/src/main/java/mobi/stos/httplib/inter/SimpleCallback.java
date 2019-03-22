package mobi.stos.httplib.inter;

public interface SimpleCallback {

    /**
     * Retorno dinânimco
     * Tipos de retorno:
     * 1. int (StatusCode), object (JSONArray, JSONObject, String)
     * 2. Exception
     * @param objects
     */
    void onCallback(Object ... objects);

}
