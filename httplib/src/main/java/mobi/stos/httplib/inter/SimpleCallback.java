package mobi.stos.httplib.inter;

public interface SimpleCallback {

    /**
     * Retorno dinânimco
     * Tipos de retorno:
     * 1. Null
     * 2. int (StatusCode), object (JSONArray, JSONObject, String)
     * 3. Exception
     * @param objects
     */
    void onCallback(Object ... objects);

}
