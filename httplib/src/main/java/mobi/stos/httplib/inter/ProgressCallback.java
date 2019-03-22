package mobi.stos.httplib.inter;

public interface ProgressCallback {

    void onProgress(int totalBytes, int read, float percent);

}
