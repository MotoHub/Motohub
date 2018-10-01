package online.motohub.util;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BasePopup {
	public static ViewGroup mViewGroup;

	public static void setupUI(View view, final Context mContext,
                               final Dialog mDialog) {

		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(mContext, mDialog);
					return false;
				}
			});
		}
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View mInnerView = ((ViewGroup) view).getChildAt(i);
				setupUI(mInnerView, mContext, mDialog);
			}
		}
	}

	public static void hideSoftKeyboard(Context mContext, Dialog mDialog) {
		try {
			if (mDialog != null) {
				InputMethodManager mInputMethodManager = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (mDialog.getCurrentFocus() != null
						&& mDialog.getCurrentFocus().getWindowToken() != null) {
					mInputMethodManager.hideSoftInputFromWindow(mDialog
							.getCurrentFocus().getWindowToken(), 0);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
