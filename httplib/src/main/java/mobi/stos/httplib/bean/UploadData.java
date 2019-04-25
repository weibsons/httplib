package mobi.stos.httplib.bean;

import java.io.File;

public class UploadData {

    private File file;
    private String fileUri;
    private String fileContentType;
    private String key;

    public UploadData() {
    }

    public UploadData(File file, String fileUri, String fileContentType, String key) {
        this.file = file;
        this.fileUri = fileUri;
        this.fileContentType = fileContentType;
        this.key = key;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
