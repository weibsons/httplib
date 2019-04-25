package mobi.stos.httplib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpUtil {

    /**
     * Convert InputStream to String
     * @param inputStream InputStream
     * @return String
     */
    public static String inputStreamToString(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
