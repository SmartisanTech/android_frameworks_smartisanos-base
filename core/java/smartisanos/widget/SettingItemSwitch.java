
package smartisanos.widget;

import android.annotation.DrawableRes;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import smartisanos.util.Utils;
import com.smartisanos.internal.R;

/**
 * Setting switcher view used in Settings and some APPs, that has the unified UI style with Settings.
 */
public class SettingItemSwitch extends RelativeLayout {
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private SwitchEx mSwitch;

    private TextView summary;

    private TextView mTitle;

    private TextView mSubTitle;

    private ImageView mInfoBtn;

    private Drawable mIconDrawable;
    private LinearLayout mTitleSummaryLayout;
    private View mSecondaryLayout;

    private Toast mToast;
    private int mDisableReasonStringId;

    public SettingItemSwitch(Context context) {
        this(context, null);
    }

    public SettingItemSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setGravity(Gravity.CENTER_VERTICAL);
        final View container = LayoutInflater.from(context).inflate(
                R.layout.setting_item_switch_layout, this, true);

        mTitleSummaryLayout = (LinearLayout) findViewById(R.id.title_layout);
        mSecondaryLayout = findViewById(R.id.secondary_layout);
        mTitle = (TextView)container.findViewById(R.id.item_title);
        summary = (TextView)container.findViewById(R.id.item_summary);
        mSwitch = (SwitchEx)container.findViewById(R.id.item_switch);
        mSubTitle = (TextView) container.findViewById(R.id.item_subtitle_text);
        mInfoBtn = (ImageView) container.findViewById(R.id.item_info_btn);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingItemSwitch,
                defStyle, 0);

        int iconId = a.getResourceId(R.styleable.SettingItemText_icon, 0);
        if (iconId != 0) {
            mIconDrawable = getResources().getDrawable(iconId);
            mIconDrawable.setBounds(0, 0,
                    mIconDrawable.getIntrinsicWidth(), mIconDrawable.getIntrinsicHeight());
        }

        mTitle.setText(a.getText(R.styleable.SettingItemSwitch_title));
        CharSequence strSummary = a.getText(R.styleable.SettingItemSwitch_summary);
        if (strSummary != null) {
            summary.setVisibility(View.VISIBLE);
            summary.setText(strSummary);
        }
        mSwitch.setChecked(a.getBoolean(R.styleable.SettingItemSwitch_isChecked, false));

        CharSequence strDescription = a.getText(R.styleable.SettingItemSwitch_subTitle);
        if (strDescription != null) {
            mSubTitle.setText(strDescription);
        }

        a.recycle();

        this.setClickable(false);
        this.setFocusable(false);

        mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mOnCheckedChangeListener != null) {
                    compoundButton.setTag(SettingItemSwitch.this.getId());
                    mOnCheckedChangeListener.onCheckedChanged(compoundButton, b);
                }
            }
        });
        mSwitch.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!hasWindowFocus()) {
                    return true;
                }
                return false;
            }
        });
        setSaveFromParentEnabled(false);
        updateTitleLayoutLocation();
        limitTitleMaxSizeIfNeed();
    }

    private void limitTitleMaxSizeIfNeed() {
        Utils.resetTextViewFontSizeAttr(getContext(), mTitle, R.dimen.settings_item_title_size);
        int titleMaxWidth = getTitleMaxWidth();
        float requireWidth = Utils.caculateTextWidth(mTitle);
        if (requireWidth > titleMaxWidth) {
            setMaxTitleSize(getResources().getDimensionPixelSize(R.dimen.common_max_size));
        }
    }

    private int getTitleMaxWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        final int switchWidth = 252;
        final int infoButtonWidth = 123;
        int titleLeftMargin = 0;
        if (mIconDrawable != null) {
            titleLeftMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_title_left_margin_with_grid);
        } else {
            titleLeftMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_title_left_margin);
        }
        int switchOccupyWidth = switchWidth + getResources().getDimensionPixelOffset(R.dimen
                .settings_item_right_widget_margin);

        int infoButtonOccupyWidth = 0;
        if (mInfoBtn.getVisibility() == View.VISIBLE) {
            infoButtonOccupyWidth = infoButtonWidth + 3 * 3; //info button wdith add left margin
        }

        return metrics.widthPixels - switchOccupyWidth - infoButtonOccupyWidth - titleLeftMargin;
    }

    private void updateTitleLayoutLocation() {
        if (mIconDrawable != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTitleSummaryLayout.getLayoutParams();
            params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_title_left_margin_with_grid);
            //set right margin for workaround, toLeftOf attribute not work when in listview.
            final int switchWidth = 252;
            int switchOccupyWidth = switchWidth + getResources().getDimensionPixelOffset(R.dimen
                    .settings_item_right_widget_margin);
            params.rightMargin = switchOccupyWidth;
        }
    }

    private boolean mPressed;

    @Override
    public void setPressed(boolean pressed) {
        if(!this.isClickable()){
            return;
        }
        super.setPressed(pressed);
        if (mPressed != pressed) {
            mPressed = pressed;
            refreshState();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (mIconDrawable != null) {
            canvas.save();
            int iconTransX = 0;
            int iconTransY = 0;
            if (mIconDrawable != null) {
                int gridWidth = getResources().getDimensionPixelOffset(R.dimen.settings_item_grid_scale);
                int gridLeftMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_grid_left_margin);
                iconTransX = gridLeftMargin + gridWidth / 2 - mIconDrawable.getIntrinsicWidth() / 2;
                iconTransY = (mSecondaryLayout.getTop() + mSecondaryLayout.getBottom() - mIconDrawable.getIntrinsicHeight()) / 2;
                canvas.translate(iconTransX, iconTransY);
                mIconDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.isChecked = isChecked();
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setChecked(savedState.isChecked);
        requestLayout();
    }

    private void refreshState() {
        if (mIconDrawable != null) {
            mIconDrawable.setState(getDrawableState());
            mIconDrawable.invalidateSelf();
        }
    }

    public void setIconResource(int iconRes) {
        mIconDrawable = getResources().getDrawable(iconRes);
        if (mIconDrawable != null) {
            mIconDrawable.setBounds(0, 0,
                    mIconDrawable.getIntrinsicWidth(), mIconDrawable.getIntrinsicHeight());
        }
        updateTitleLayoutLocation();
        limitTitleMaxSizeIfNeed();
        invalidate();
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        setPadding(0, 0, 0, 0);
        setBackground(mContext.getDrawable(resid));
    }

    /**
     * @param maxTextSize unit px
     */
    public void setMaxTitleSize(float maxTextSize) {
        Utils.setMaxTextSizeForTextView(mTitle, maxTextSize);
    }

    /**
     * @param maxTextSize unit px
     */
    public void setMaxSummarySize(float maxTextSize) {
        Utils.setMaxTextSizeForTextView(summary, maxTextSize);
    }

    public void setTitle(CharSequence s) {
        Utils.resetTextViewFontSizeAttr(getContext(), mTitle, R.dimen.settings_item_title_size);
        mTitle.setText(s);
        limitTitleMaxSizeIfNeed();
    }

    public void setTitle(int titleRes) {
        setTitle(getContext().getString(titleRes));
    }

    public void setSummary(CharSequence s) {
        summary.setVisibility(View.VISIBLE);
        summary.setText(s);
    }

    public void setSummary(int s) {
        summary.setVisibility(View.VISIBLE);
        summary.setText(s);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public void setChecked(boolean isChecked) {
        mSwitch.setChecked(isChecked);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    public SwitchEx getSwitch() {
        return mSwitch;
    }

    public CharSequence getTitle() {
        return mTitle.getText();
    }

    public TextView getTitleView() {
        return mTitle;
    }

    public void setupInfoButton(boolean visible, OnClickListener listener) {
        if (visible) {
            mInfoBtn.setVisibility(View.VISIBLE);
            mInfoBtn.setOnClickListener(listener);
        } else {
            mInfoBtn.setVisibility(View.GONE);
        }
        limitTitleMaxSizeIfNeed();
    }

    public void setSwitchSubtitle(CharSequence s) {
        mSubTitle.setText(s);
        limitTitleMaxSizeIfNeed();
    }

    private void setSwitchVisibility(int visibility) {
        mSwitch.setVisibility(visibility);
        if (visibility == View.GONE) {
            mSubTitle.setVisibility(View.VISIBLE);
        } else {
            mSubTitle.setVisibility(View.GONE);
        }
    }

    /**
     * replaced by {@link #setEnabled} method
     */
    @Deprecated
    public void setSwitchEnable(boolean enabled) {
        this.setEnabled(enabled);
        mSwitch.setEnabled(enabled);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mSwitch.setEnabled(enabled);
    }

    /**
     * set string resource id which will display when disabled switch be clicked
     */
    public void setSwitchDisabledTips(int stringId) {
        mDisableReasonStringId = stringId;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mSwitch != null && mSwitch.isEnabled() == false && mDisableReasonStringId > 0) {
            Rect switchRect = new Rect();
            mSwitch.getGlobalVisibleRect(switchRect);
            if (switchRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                if (mToast == null || mToast.getView().getWindowVisibility() != View.VISIBLE) {
                    mToast = Toast.makeText(getContext(), mDisableReasonStringId, Toast.LENGTH_LONG);
                    mToast.show();
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    static class SavedState extends BaseSavedState {

        boolean isChecked;

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel source) {
            super(source);
            isChecked = (Boolean) source.readValue(null);
        }

        /**
         * Constructor called from {@link #onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeValue(isChecked);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
    }

}
