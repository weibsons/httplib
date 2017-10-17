package mobi.stos.httplib.inter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Weibson on 04/10/2017.
 */

public interface FutureCallback {

    void onBeforeExecute();

    void onAfterExecute();

    void onSuccess(int responseCode, @Nullable Object object);

    void onFailure(@NonNull Exception exception);

}
