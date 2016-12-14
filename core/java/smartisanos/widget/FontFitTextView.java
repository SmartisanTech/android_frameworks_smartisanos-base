package smartisanos.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.smartisanos.internal.R;

/**
 * @hide
 */
public class FontFitTextView extends TextView {

    private float mFitMinSize;
    private final Paint mFitPaint;

    public FontFitTextView(Context context) {
        this(context, null);
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontFitTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFitPaint = new Paint();
        mFitMinSize = getContext().getResources().getDimension(R.dimen.font_fit_min_size);
    }

    public void setMinTextSize(float px) {
        mFitMinSize = px;
        if (px > getTextSize()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        }
        refitText(getText().toString(), getWidth());
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textWidth) {
        if (textWidth <= 0 || mFitPaint == null) return;

        final int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
        mFitPaint.set(getPaint());
        if (mFitPaint.measureText(text) > targetWidth) {
            float hi = getTextSize();
            float lo = mFitMinSize;
            if (hi > lo) {
                final float threshold = 0.5f; // How close we have to be
                while (hi - lo > threshold) {
                    float size = (hi + lo) / 2;
                    mFitPaint.setTextSize(size);
                    if (mFitPaint.measureText(text) >= targetWidth)
                        hi = size; // too big
                    else
                        lo = size; // too small
                }
                if (lo > mFitMinSize + threshold) {
                    final float targetHeight = getMeasuredHeight();
                    if (getFontHeight() > targetHeight) {
                        hi = lo;
                        lo = mFitMinSize;
                        while (hi - lo > threshold) {
                            float size = (hi + lo) / 2;
                            mFitPaint.setTextSize(size);
                            if (getFontHeight() >= targetHeight)
                                hi = size; // too big
                            else
                                lo = size; // too small
                        }
                    }
                }
                // Use lo so that we undershoot rather than overshoot
                setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
            }
        }
    }

    private float getFontHeight() {
        Paint.FontMetrics fm = mFitPaint.getFontMetrics();
        return fm.bottom - fm.top;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        refitText(getText().toString(), getMeasuredWidth());
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), getWidth());
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(getText().toString(), w);
        }
    }
}