package online.motohub.util;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private File mFile;
    private Integer NotificationID;
    private UploadCallbacks mListener;

    public ProgressRequestBody(final File file, final UploadCallbacks listener, final int NotificationID) {
        mFile = file;
        mListener = listener;
        this.NotificationID = NotificationID;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("video/*");
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength, NotificationID));

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage, int notificationID);

        void onError();

        void onFinish();
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        private Integer notificationID;

        public ProgressUpdater(long uploaded, long total, int NotificationID) {
            mUploaded = uploaded;
            mTotal = total;
            this.notificationID = NotificationID;
        }

        @Override
        public void run() {
            try {
                mListener.onProgressUpdate((int) (100 * mUploaded / mTotal), notificationID);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}