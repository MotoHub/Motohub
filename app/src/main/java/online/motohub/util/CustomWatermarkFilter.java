package online.motohub.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.daasuu.gpuv.egl.filter.GlOverlayFilter;

public class CustomWatermarkFilter extends GlOverlayFilter {

    private Bitmap bitmap;
    private Position position = Position.LEFT_TOP;

    public CustomWatermarkFilter(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public CustomWatermarkFilter(Bitmap bitmap, Position position) {
        this.bitmap = bitmap;
        this.position = position;
    }

    @Override
    protected void drawCanvas(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            switch (position) {
                case LEFT_TOP:
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    break;
                case LEFT_BOTTOM:
                    //canvas.drawBitmap(bitmap, 0, canvas.getHeight() - bitmap.getHeight(), null);
                    canvas.drawBitmap(bitmap, 10, canvas.getHeight() - bitmap.getHeight() - 100, null);
                    break;
                case RIGHT_TOP:
                    canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), 0, null);
                    break;
                case RIGHT_BOTTOM:
                    //canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), canvas.getHeight() - bitmap.getHeight(), null);
                    canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth() - 10, canvas.getHeight() - bitmap.getHeight() - 10, null);
                    break;
            }
        }
    }

    public enum Position {
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }
}