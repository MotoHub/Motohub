package online.motohub.bl;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import retrofit2.Call;


public class MHFileCacheImplementor implements FileCacheImplementor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final File parentFile;

    private final String TAG = "FILE_CACHE";

    public MHFileCacheImplementor(File filecacheDir) {
        this.parentFile = filecacheDir;
        if (!this.parentFile.exists()) {
            if (!this.parentFile.mkdir()) {
                Log.e(TAG, "Not able to create a directory to save file cache");
            }
        }
    }

    // Cache response
    @Override
    public void saveResponseBody(Response response, String overRideFileName) {
        try {
            if (response.isSuccessful() && response.body() != null) {
                String fileName = overRideFileName == null ? urlToKey(response.request()) : overRideFileName;
                File file = new File(parentFile, fileName);

                InputStream inputStream = null;
                try {
                    BufferedSource source = response.body().source();
                    // clone buffer before reading from it
                    source.request(Long.MAX_VALUE); // request the entire body.
                    Buffer buffer = source.buffer();
                    byte[] responseByte = buffer.clone().readByteArray();

                    String contentEncodingHeader = response.header("Content-Encoding");
                    if (contentEncodingHeader != null && contentEncodingHeader.equals("gzip")) {
                        // Read the zipped contents.
                        inputStream = new GZIPInputStream(new ByteArrayInputStream(responseByte));
                    } else {
                        // Read the normal contents.
                        inputStream = new ByteArrayInputStream(responseByte);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    inputStream = null;
                }

                if (inputStream != null) {
                    BufferedInputStream stream = new BufferedInputStream(inputStream);
                    FileOutputStream fooStream = null;
                    try {
                        fooStream = new FileOutputStream(file, false); // true to append false to overwrite.
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = stream.read(buf)) > 0) {
                            fooStream.write(buf, 0, len);
                        }
                        fooStream.flush();
                        inputStream.close();
                        fooStream.close();
                        Log.e(TAG, "Saved file succesfully " + fileName);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        if (fooStream != null) fooStream.close();
                        inputStream.close();
                    }
                } else {
                    Log.e(TAG, "Failed to read response from server");
                }
            } else {
                Log.e(TAG, "Either the response is not success or it is not valid response from server");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public static String urlToKey(Request request) {
        String header = request.header("SAVE_RESPONSE_AS");
        if (TextUtils.isEmpty(header)) {
            return md5Hex(request.url().toString());
        } else {
            return header.trim();
        }
    }

    /**
     * Returns a 32 character string containing an MD5 hash of {@code s}.
     */
    public static String md5Hex(String s) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5bytes = messageDigest.digest(s.getBytes("UTF-8"));
            return ByteString.of(md5bytes).hex();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public <T> T getCachedResponseBody(Call<T> call, Class<T> classObject) {
        return getCachedResponseBody(call, (Type) classObject);
    }

    public <T> T getCachedResponseBody(Call<T> call, Type type) {
        Request request = call.request();
        if (request != null) {
            String fileName = urlToKey(request);
            File file = new File(parentFile, fileName);
            if (file.exists()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    Gson gson = new GsonBuilder().create();
                    T o = gson.fromJson(reader, type);
                    if (o != null) {
                        Log.e(TAG, "Found file and parse successfully");
                    }
                    reader.close();
                    return o;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return null;
    }


    @Override
    public void deleteAll() {
        if (parentFile.exists()) {
            File[] children = parentFile.listFiles();
            for (int i = 0; i < children.length; i++) {
                children[i].delete();
            }
        }
    }
}
