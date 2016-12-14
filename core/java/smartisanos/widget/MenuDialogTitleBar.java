package smartisanos.widget;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.smartisanos.internal.R;

public class MenuDialogTitleBar extends RelativeLayout {
    public MenuDialogTitleBar(Context context) {
        super(context);
    }

    public MenuDialogTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuDialogTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private TextView title;
    private TextView rightButton;
    private TextView leftButton;

    private float mTitleSize; //sp
    private float mBtnSize; //sp


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        title = (TextView) findViewById(R.id.title);
        rightButton = (TextView) findViewById(R.id.btn_cancel_right);
        leftButton = (TextView) findViewById(R.id.btn_cancel_left);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mTitleSize = title.getTextSize() / metrics.scaledDensity;
        mBtnSize = rightButton.getTextSize() / metrics.scaledDensity;
    }

    public void setTitle(CharSequence text) {
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleSize);
        title.setText(text);
    }

    public void setTitleSingleLine(boolean singleLine) {
        title.setSingleLine(singleLine);
    }

    public void setRightButtonText(CharSequence text) {
        rightButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, mBtnSize);
        rightButton.setText(text);
    }

    public void setLeftButtonText(CharSequence text) {
        leftButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, mBtnSize);
        leftButton.setText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (metrics.density == metrics.scaledDensity) {
            return;
        }

        boolean adjustBtn = false;
        boolean reMeasure = false;
        final Layout layout = title.getLayout();
        if (layout != null) {
            final int lineCount = layout.getLineCount();
            if (lineCount > 0) {
                final int ellipsisCount = layout.getEllipsisCount(lineCount - 1);
                if (ellipsisCount > 0) {
                    if (adjustTextSize(title, mTitleSize)) {
                        reMeasure = true;
                    } else {
                        adjustBtn = true;
                    }
                }
            }
        }
        if (adjustBtn) {
            if (adjustTextSize(leftButton, mBtnSize)) {
                reMeasure = true;
            }
            if (adjustTextSize(rightButton, mBtnSize)) {
                reMeasure = true;
            }
        }
        if (reMeasure) {
            onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean adjustTextSize(TextView textView, float standarTextSize) {
        if (textView.getVisibility() == View.GONE) {
            return false;
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (textView.getTextSize() <= standarTextSize * metrics.density) {
            return false;
        }
        float newTextSize = textView.getTextSize() * 0.9F;
        if (newTextSize < standarTextSize * metrics.density) {
            newTextSize = standarTextSize * metrics.density;
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        return true;
    }
}
