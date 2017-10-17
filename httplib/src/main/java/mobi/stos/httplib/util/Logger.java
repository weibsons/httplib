package mobi.stos.httplib.util;

import android.util.Log;

/**
 * Created by Weibson on 04/10/2017.
 */

public class Logger {

    public static final String TAG = "HttpAsync";
    public static boolean debug = false;

    public static void d(String message) {
        if (debug) {
            Log.d(TAG, message);
        }
    }

    public static void d(String message, Throwable throwable) {
        if (debug) {
            Log.d(TAG, message, throwable);
        }
    }

}
