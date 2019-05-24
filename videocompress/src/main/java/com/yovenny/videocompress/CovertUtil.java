package com.yovenny.videocompress;

import java.nio.ByteBuffer;

public class CovertUtil {

    static {
        System.loadLibrary("compress");
    }

    public native static int convertVideoFrame(ByteBuffer src, ByteBuffer dest, int destFormat, int width, int height, int padding, int swap);
}
