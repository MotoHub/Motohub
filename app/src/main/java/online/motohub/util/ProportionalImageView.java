package online.motohub.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by Prithiv on 4/23/2018.
 */

public class ProportionalImageView extends ImageView {

    public ProportionalImageView(Context context) {
        super(context);
    }

    public ProportionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProportionalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            if (((float) (d.getIntrinsicWidth() / d.getIntrinsicHeight())) < 0.752) {
                int w = MeasureSpec.getSize(widthMeasureSpec);
                int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
                setMeasuredDimension(w, h);
            } else {
                int w = MeasureSpec.getSize(widthMeasureSpec);
                int h = w * d.getIntrinsicHeight() / d.getIntrinsicWidth();
                setMeasuredDimension(w, h);
            }
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}