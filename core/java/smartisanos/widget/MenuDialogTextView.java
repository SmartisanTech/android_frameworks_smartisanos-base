package smartisanos.widget;

import com.smartisanos.internal.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class MenuDialogTextView extends FontFitTextView {

    private float limitX;
    private int screenWidth;

    public MenuDialogTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        limitX = 93 * metrics.density + 0.5f;
        screenWidth = Math.min(metrics.widthPixels, metrics.heightPixels);
        setMinTextSize(getResources().getDimension(R.dimen.menu_dialog_item_text_min_size));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int width = getWidth();
        //if the device in portrait status, we don't need to process it.
        if (width <= screenWidth) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        int action = event.getActionMasked();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (x < limitX || x > (width - limitX)) {
                return true;
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
            //if current point leave the satisfy area, we want the button not still in press status.
            if (x < limitX || x > (width - limitX)) {
                setPressed(false);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

}
