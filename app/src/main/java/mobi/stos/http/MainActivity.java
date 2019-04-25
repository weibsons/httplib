package mobi.stos.http;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;

import mobi.stos.httplib.HttpUploadAsync;
import mobi.stos.httplib.inter.FutureCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            this.upload();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 9);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.upload();
        }
    }

    private void upload() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "SMS/Pic01.png");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("guid", "Bm29q");

            HttpUploadAsync httpUploadAsync = new HttpUploadAsync(new URL("https://bureau.stos.mobi/web/upload.php"));
            httpUploadAsync.setDebug(true);
            httpUploadAsync.addParam("json", jsonObject.toString());
            httpUploadAsync.addUploadData(file, file.getAbsolutePath(), "image/png", "arquivo");
            httpUploadAsync.addOnSuccessCallback(objects -> {
                Log.v("MAIN", "onSuccess -> " + objects[0]);
                Log.v("MAIN", "onSuccess -> " + objects[1]);
            });
            httpUploadAsync.upload();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
