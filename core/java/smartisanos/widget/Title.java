
package smartisanos.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.res.ColorStateList;

import com.smartisanos.internal.R;

/**
 * Title: used in Settings app and some other apps that containing the same titlebar with Settings.
 * <p/>
 * Note: don't set text for backBtn & okBtn by mBackButton.setText() or mOkButton.setText() in your code,
 * it will cause UI issue. Call {@link Title#setBackButtonText} & {@link Title#setOkButtonText} instead
 */
public class Title extends RelativeLayout {

    private static final String TAG = "Title";
    public static final String EXTRA_BACK_BTN_TEXT = "back_text";
    public static final String EXTRA_BACK_BTN_RES_ID = "back_text_id";
    public static final String EXTRA_BACK_BTN_RES_NAME = "back_text_res_name";
    public static final String EXTRA_TITLE_TEXT = "title";
    public static final String EXTRA_TITLE_TEXT_ID = "title_id";

    private TextView mTitle;

    private TextView mBackButton;

    private TextView mOkButton;

    private View mPlaceHolderView;
    private int screenWidth;
//    private static int MAX_BACK_BTN_WIDTH;

    public Title(Context context) {
        super(context);
    }

    public Title(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Title(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final View container = LayoutInflater.from(context)
                .inflate(R.layout.title_layout, this, true);

        mTitle = (TextView) container.findViewById(R.id.tv_title);
        mBackButton = (TextView) container.findViewById(R.id.btn_back);
        mOkButton = (TextView) container.findViewById(R.id.btn_ok);
        mPlaceHolderView = container.findViewById(R.id.place_holder);

        //modify to 101.7dip   [720P : 191px ;   1080P : 305px]
//        MAX_BACK_BTN_WIDTH = getResources().getDimensionPixelSize(R.dimen.title_back_btn_max_width);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Title, defStyle, 0);

        CharSequence strBackButton = a.getText(R.styleable.Title_backText);

        if (strBackButton != null) {
            mBackButton.setText(strBackButton);
        }
        ColorStateList backBtnTextColor = a.getColorStateList(R.styleable.Title_backTextColor);
        int backBtnTextSize = a.getDimensionPixelSize(R.styleable.Title_backTextSize, -1);
        if (backBtnTextColor != null) {
            mBackButton.setTextColor(backBtnTextColor);
        }
        if (backBtnTextSize > 0) {
            mBackButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, backBtnTextSize);
        }

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;

        CharSequence strOkBtn = a.getText(R.styleable.Title_okText);
        if (strOkBtn != null) {
            mOkButton.setVisibility(View.VISIBLE);
            mOkButton.setText(strOkBtn);
        }
        ColorStateList okBtnTextColor = a.getColorStateList(R.styleable.Title_okTextColor);
        int okBtnTextSize = a.getDimensionPixelSize(R.styleable.Title_okTextSize, -1);
        if (okBtnTextColor != null) {
            mOkButton.setTextColor(okBtnTextColor);
        }
        if (okBtnTextSize > 0) {
            mOkButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, okBtnTextSize);
        }

        CharSequence strTitle = a.getText(R.styleable.Title_title);
        if (strTitle != null) {
            mTitle.setText(strTitle);
        }
        ColorStateList titleColor = a.getColorStateList(R.styleable.Title_titleColor);
        int titleSize = a.getDimensionPixelSize(R.styleable.Title_titleSize, -1);
        if (titleColor != null) {
            mTitle.setTextColor(titleColor);
        }
        if (titleSize > 0) {
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        }
        setOnPreDrawListener();
        a.recycle();
        //set default background
        setBackgroundResource(R.drawable.title_bar_bg);
    }

    private static final int MSG_SET_TITLE_ALIGN = 0;
    private static final int DELAY = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SET_TITLE_ALIGN) {
                setOnPreDrawListener();
            }
        }
    };

    private void schedule2SetTitleAlign() {
        mHandler.removeMessages(MSG_SET_TITLE_ALIGN);
        mHandler.sendEmptyMessageDelayed(MSG_SET_TITLE_ALIGN, DELAY);
    }

    private void setOnPreDrawListener() {
        getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    private void removeOnPreDrawListener() {
        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
    }

    final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            removeOnPreDrawListener();
            setTitleAlign();
            return true;
        }
    };

    private void setTitleAlign() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTitle.getLayoutParams();
        float backBtnWidth = mBackButton.getWidth();
        float okBtnWidth;
        if (mOkButton.getVisibility() != View.GONE && !TextUtils.isEmpty(mOkButton.getText())) {
            okBtnWidth = mOkButton.getWidth();
        } else {
            okBtnWidth = 0;
        }
//        Log.d(TAG, "========backBtnWidth:" + backBtnWidth + "  okBtnWidth:" + okBtnWidth);
        if (backBtnWidth == 0 && okBtnWidth == 0) {
            setPlaceHolderWidth(0);
            mTitle.setGravity(Gravity.CENTER);
            mTitle.setPadding(0, 0, 0, 0);
            return;
        }

        float midWidth = screenWidth - (Math.max(backBtnWidth, okBtnWidth)) * 2;
        float titleWidth = caculateTextWidth(mTitle);
        if (midWidth > (titleWidth + 5)) {//there are enough space to contain title text.
            setPlaceHolderWidth((int) backBtnWidth);
            //in some case, title maybe changed in code dynamicly.
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mTitle.setGravity(Gravity.CENTER);
            mTitle.setPadding(0, 0, 0, 0);
        } else {
            int placeHolderWidth;
            if (okBtnWidth == 0 || mOkButton.getVisibility() == View.GONE) {//ok btn text is empty or set to gone by user
                placeHolderWidth = getResources().getDimensionPixelSize(R.dimen.title_place_holder_min_width);
                setPlaceHolderWidth(placeHolderWidth);
            } else {
                placeHolderWidth = (int) okBtnWidth;
                setPlaceHolderWidth(placeHolderWidth);
            }
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mTitle.setGravity(Gravity.LEFT);
            mTitle.setPadding((int)backBtnWidth + 1, 0, placeHolderWidth, 0);
        }
    }

    private void setPlaceHolderWidth(int width) {
        ViewGroup.LayoutParams lp = mPlaceHolderView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(width, 1);
        } else {
            lp.width = width;
        }
        mPlaceHolderView.setLayoutParams(lp);
    }

    private float caculateTextWidth(TextView tv) {
        Paint paint = new Paint();
        paint.setTextSize(tv.getTextSize());
        CharSequence text = tv.getText();
        if (text == null)
            return 0;
        return paint.measureText(tv.getText().toString());
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);

        schedule2SetTitleAlign();
    }

    public void setTitle(int titleId) {
        setTitle(getResources().getString(titleId));
    }

    public void setTitleColor(int colorValue) {
        mTitle.setTextColor(colorValue);
    }

    public TextView getTitle() {
        return mTitle;
    }

    /**
     * return View instead of TextView, because i don't
     * want you to set text using it directly
     * @return
     */
    public View getOkButton() {
        return mOkButton;
    }

    public View getBackButton() {
        return mBackButton;
    }

    public void setBackButtonTextByIntent(Intent intent) {
        try {
            if (intent != null) {
                String backText = intent.getStringExtra(EXTRA_BACK_BTN_TEXT);
                if (!TextUtils.isEmpty(backText)) {
                    setBackButtonText(backText);
                } else {
                    int backId = 0;
                    String resName = intent.getStringExtra(EXTRA_BACK_BTN_RES_NAME);
                    if(!TextUtils.isEmpty(resName)) {
                        Context context = this.getContext();
                        backId = context.getResources().getIdentifier(resName, "string", context
                                .getPackageName());
                    }
                    if(backId == 0) {
                        backId = intent.getIntExtra(EXTRA_BACK_BTN_RES_ID, -1);
                    }
                    if (backId > 0) {
                        setBackButtonTextByRes(backId);
                    } else {
                        setBackButtonTextByRes(R.string.title_button_text_back);
                    }
                }
            }
        } catch (RuntimeException e) {
            setBackButtonTextByRes(R.string.title_button_text_back);
            e.printStackTrace();
        }
    }

    public void setBackButtonTextByRes(int resId) {
        if (resId != -1) {
            setBackButtonText(getResources().getString(resId));
        }
    }

    public void setBackButtonText(CharSequence s) {
        if (mBackButton != null) {
            mBackButton.setText(s);
            schedule2SetTitleAlign();
        }
    }

    public void setBackButtonText(int resId) {
        setBackButtonText(getResources().getString(resId));
    }

    public void setBackButtonBackground(Drawable drawable) {
        mBackButton.setBackground(drawable);
        schedule2SetTitleAlign();
    }

    public void setBackButtonBackgroundResource(int resId) {
        mBackButton.setBackgroundResource(resId);
        schedule2SetTitleAlign();
    }

    public void setBackButtonTextGravity(int gravity) {
        mBackButton.setGravity(gravity);
    }

    public void resetBackButtonTextGravityAsDefault() {
        mBackButton.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
    }

    public void setBackButtonTextColor(int colorValue) {
        mBackButton.setTextColor(colorValue);
    }

    public void setBackButtonTextColor(ColorStateList colors) {
        mBackButton.setTextColor(colors);
    }

    public void resetBackButtonTextColorAsDefault() {
        setBackButtonTextColor(getResources().getColorStateList(R.color.title_bar_button_text_colorlist));
    }

    public void setOkButtonText(CharSequence s) {
        if (mOkButton != null) {
            mOkButton.setText(s);
            schedule2SetTitleAlign();
        }
    }

    public void setOkButtonText(int resId) {
        setOkButtonText(getResources().getString(resId));
    }

    public void setOkButtonBackground(Drawable drawable) {
        mOkButton.setBackground(drawable);
        schedule2SetTitleAlign();
    }

    public void setOkButtonBackgroundResource(int resId) {
        mOkButton.setBackgroundResource(resId);
        schedule2SetTitleAlign();
    }

    public void setOkButtonShadow(){
        mOkButton.setShadowLayer(1, 0, -2, 0x4c7f3300);
    }

    public void setOkButtonTextColor(int colorValue) {
        mOkButton.setTextColor(colorValue);
    }

    public void setOkButtonTextColor(ColorStateList colors) {
        mOkButton.setTextColor(colors);
    }

    public void resetOkButtonTextColorAsDefault() {
        setOkButtonTextColor(getResources().getColorStateList(R.color.title_bar_highlight_button_text_colorlist));
    }

    public void setBackButtonListener(OnClickListener listener) {
        mBackButton.setOnClickListener(listener);
    }

    public void setOkButtonListener(OnClickListener listener) {
        mOkButton.setOnClickListener(listener);
    }

    public void updateBackButtonEnableState(boolean enable) {
        mBackButton.setEnabled(enable);
        //mBackButton.getBackground().setAlpha(enable ? 255 : 255 / 2);
    }

    public void updateOkButtonEnableState(boolean enable) {
        mOkButton.setEnabled(enable);
        //mOkButton.getBackground().setAlpha(enable ? 255 : 255 / 2);
    }
}
