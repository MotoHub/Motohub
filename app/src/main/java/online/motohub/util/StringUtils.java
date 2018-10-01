package online.motohub.util;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

public class StringUtils {

    private static StringUtils mStringUtils;

    private StringUtils() {}

    public static StringUtils getInstance() {
        if(mStringUtils == null) {
            mStringUtils = new StringUtils();
        }
        return mStringUtils;
    }

    public static String encodeString(String text) {
        String mEncodedText = "";
        try {
            URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            mEncodedText = e.getMessage();
            e.printStackTrace();
        }
        return mEncodedText;
    }

    public static String decodeString(String text) {
        String mDecodedText = "";
        try {
            URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            mDecodedText = e.getMessage();
            e.printStackTrace();
        }
        return mDecodedText;
    }
    public static String genRandomStreamName(Context mContext) {
        int passwordSize = 5;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < passwordSize; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String userName = PreferenceUtils.getInstance(mContext).getStrData(PreferenceUtils.FIRST_NAME);
        if (userName == null || userName.isEmpty())
            userName = "MotoHUB";
        String mStream = userName.trim() + sb.toString();
        return mStream;
    }
}
