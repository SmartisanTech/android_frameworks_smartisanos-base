
package smartisanos.app;

import com.smartisanos.internal.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Dialog;
/**
 * @hide
 */
public class SmartisanProgressDialog extends Dialog {

    private int mTitleColor = 0x9C000000;
    private int mMessageColor = 0x9C000000;
    private int mSystemUiVisibility;

    private TextView mTitleView;
    private ProgressBar mProgress;
    private TextView mMessageView;
    private LinearLayout mContent;

    private CharSequence mTitle;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private Drawable mBackground;
    private boolean mHideProgressBar = false;
    private boolean mIsDarkTheme = false;
    private boolean mHasSetTitleColor;
    private boolean mHasSetMessageColor;
    private Context mContext;

    public SmartisanProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    public static SmartisanProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        SmartisanProgressDialog dialog = new SmartisanProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.smartisan_progress_dialog, null);
        mTitleView = (TextView) view.findViewById(R.id.progress_dialog_title);
        mProgress = (ProgressBar) view.findViewById(com.android.internal.R.id.progress);
        mMessageView = (TextView) view.findViewById(com.android.internal.R.id.message);
        mContent = (LinearLayout) view.findViewById(R.id.progress_dialog_content);
        mProgress.setIndeterminate(true);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setContentView(view);
        getWindow().setBackgroundDrawableResource(com.android.internal.R.color.transparent);
    }

    @Override
    public void onStart() {
        mTitleView.setText(mTitle);
        mTitleView.setTextColor(mTitleColor);
        mTitleView.setVisibility(mTitle == null ? View.GONE : View.VISIBLE);
        mMessageView.setText(mMessage);
        mMessageView.setTextColor(mMessageColor);
        mMessageView.setVisibility(mMessage == null ? View.GONE : View.VISIBLE);
        if (mBackground != null) {
            mContent.setBackground(mBackground);
        } else if (!mIsDarkTheme) {
            mContent.setBackground(getContext().getResources().getDrawable(R.drawable.smartisan_progress_dialog_bg));
        } else {
            mContent.setBackground(getContext().getResources().getDrawable(R.drawable.smartisan_progress_dialog_bg_dark));
        }
        Drawable progressDrawable = getContext().getResources().getDrawable(R.drawable.progress_medium_smartisanos_light);
        if (mIndeterminateDrawable != null) {
            progressDrawable = mIndeterminateDrawable;
        } else if (!mIsDarkTheme) {
            progressDrawable = getContext().getResources().getDrawable(R.drawable.progress_medium_smartisanos_light);
        } else {
            progressDrawable = getContext().getResources().getDrawable(R.drawable.progress_medium_smartisanos_dark);
        }
        updateDrawableBounds(progressDrawable,mProgress.getWidth(),mProgress.getHeight());
        mProgress.setIndeterminateDrawable(progressDrawable);

        if(mHideProgressBar){
            mProgress.setIndeterminate(false);
            mProgress.setIndeterminateDrawable(null);
            mProgress.setVisibility(View.GONE);
        }
        mContent.getRootView().setSystemUiVisibility(mSystemUiVisibility);
    }

    private void updateDrawableBounds(Drawable progressDrawable,int w, int h) {
        boolean onlyIndeterminate = true;
        Rect rect = new Rect();
        progressDrawable.getPadding(rect);
        int paddingLeft = rect.left;
        int paddingRight = rect.right;
        int paddingTop = rect.top;
        int paddingBottom = rect.bottom;
        w -= paddingRight + paddingLeft;
        h -= paddingTop + paddingBottom;

        int right = w;
        int bottom = h;
        int top = 0;
        int left = 0;

        if (progressDrawable != null) {
            if (onlyIndeterminate && !(progressDrawable instanceof AnimationDrawable)) {
                final int intrinsicWidth = progressDrawable.getIntrinsicWidth();
                final int intrinsicHeight = progressDrawable.getIntrinsicHeight();
                final float intrinsicAspect = (float) intrinsicWidth / intrinsicHeight;
                final float boundAspect = (float) w / h;
                if (intrinsicAspect != boundAspect) {
                    if (boundAspect > intrinsicAspect) {
                        // New width is larger. Make it smaller to match height.
                        final int width = (int) (h * intrinsicAspect);
                        left = (w - width) / 2;
                        right = left + width;
                    } else {
                        // New height is larger. Make it smaller to match width.
                        final int height = (int) (w * (1 / intrinsicAspect));
                        top = (h - height) / 2;
                        bottom = top + height;
                    }
                }
            }
            progressDrawable.setBounds(left, top, right, bottom);
        }
    }

    public void setDarkTheme(boolean isDark) {
        mIsDarkTheme = isDark;
        if (!mIsDarkTheme) {
            if (!mHasSetTitleColor)
                mTitleColor = 0x9C000000;
            if (!mHasSetMessageColor)
                mMessageColor = 0x9C000000;
        } else {
            if (!mHasSetTitleColor)
                mTitleColor = Color.WHITE;
            if (!mHasSetMessageColor)
                mMessageColor = Color.WHITE;
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(mContext.getText(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(mTitle);
            mTitleView.setVisibility(mTitle == null ? View.GONE : View.VISIBLE);
        }
    }

    public void setIndeterminateDrawableResource(int resId) {
        setIndeterminateDrawable(mContext.getResources().getDrawable(resId));
    }

    public void setIndeterminateDrawable(Drawable d) {
        mIndeterminateDrawable = d;
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(mIndeterminateDrawable);
            mProgress.requestLayout();
        }
    }

    public void setHideProgressBar(boolean bool) {
        mHideProgressBar = bool;
    }

    public void setMessage(int msgId) {
        setMessage(mContext.getText(msgId));
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
        if (mMessageView != null) {
            mMessageView.setText(mMessage);
            mMessageView.setVisibility(mMessage == null ? View.GONE : View.VISIBLE);
        }
    }

    public void setBackgroundResource(int resId) {
        setBackground(mContext.getResources().getDrawable(resId));
    }

    public void setBackground(Drawable d) {
        mBackground = d;
    }

    public void setSystemUiVisibility(int visibility) {
        mSystemUiVisibility = visibility;
    }

    public void setTitleColor(int color) {
        mHasSetTitleColor = true;
        mTitleColor = color;
    }

    public void setMessageColor(int color) {
        mHasSetMessageColor = true;
        mMessageColor = color;
    }

    public void setProgress(int value) {
    }
}
