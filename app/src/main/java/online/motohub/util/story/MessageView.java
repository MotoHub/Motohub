package online.motohub.util.story;


import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import online.motohub.R;

public class MessageView extends LinearLayout {

    private TextView message;
    private TextView title;

    public MessageView(Context context) {
        this(context, null);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        inflate(context, R.layout.control_view, this);
        ViewGroup content = findViewById(R.id.content);
        inflate(context, R.layout.spinner_text, content);
        title = findViewById(R.id.title);
        message = (TextView) content.getChildAt(0);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }
}
