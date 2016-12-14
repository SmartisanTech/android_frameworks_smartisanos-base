package smartisanos.util;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.util.TypedValue;
import android.widget.TextView;

public class Utils {
    /**
     * @param textView
     * @param dimenResId the dimen of textSize for textView
     */
    public static void resetTextViewFontSizeAttr(Context context, TextView textView, int dimenResId) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimensionPixelSize(dimenResId));
    }

    public static float caculateTextWidth(TextView tv) {
        Paint paint = new Paint();
        paint.setTextSize(tv.getTextSize());
        CharSequence text = tv.getText();
        if (text == null)
            return 0;
        return paint.measureText(tv.getText().toString());
    }

    /**
     * @param target
     * @param maxSize  unit px
     */
    public static void setMaxTextSizeForTextView(TextView target, float maxSize) {
    }

    public static boolean isTextEllipsized(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            int lines = layout.getLineCount();
            if (lines > 0) {
                int ellipsisCount = layout.getEllipsisCount(lines - 1);
                if (ellipsisCount > 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
