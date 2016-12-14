
package smartisanos.widget;

import android.annotation.DrawableRes;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import smartisanos.util.Utils;
import com.smartisanos.internal.R;

/**
 * Setting item view used in Settings and some APPs, that has the unified UI style with Settings.
 */
public class SettingItemText extends RelativeLayout {

    private ImageView mIcon;
    private TextView mTitle;
    private TextView mSubTitle;
    private TextView mSummary;
    private ImageView mArrowIcon;
    private Drawable mArrowDrawable;
    private Drawable mBadgeDrawable;
    private int currentBadgeRes = -1;
    private View mTitleSummaryLayout;
    private View mSecondaryLayout;
    private float mDensity;

    private boolean mShowBadge = false;
    private Toast mToast;
    private int mDisableReasonStringId;


    public SettingItemText(Context context) {
        this(context, null);
    }

    public SettingItemText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final View container = LayoutInflater.from(context).inflate(
                R.layout.setting_item_text_layout, this, true);
        setGravity(Gravity.CENTER_VERTICAL);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mDensity = metrics.density;
        mTitleSummaryLayout = findViewById(R.id.title_summary_layout);
        mSecondaryLayout = findViewById(R.id.secondary_layout);

        mIcon = (ImageView) container.findViewById(R.id.item_icon);
        mTitle = (TextView)container.findViewById(R.id.item_title);
        mSubTitle = (TextView)container.findViewById(R.id.item_subtitle);
        mSummary = (TextView) container.findViewById(R.id.item_summary);
        mArrowIcon = (ImageView) container.findViewById(R.id.item_arrow);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SettingItemText, defStyle, 0);

        // Icon display
        int iconId = a.getResourceId(R.styleable.SettingItemText_icon, 0);
        if (iconId > 0) {
            mIcon.setImageResource(iconId);
            mIcon.setVisibility(View.VISIBLE);
        }

        // Text display
        mTitle.setText(a.getText(R.styleable.SettingItemText_title));
        int titleMaxWidth = a.getDimensionPixelOffset(R.styleable.SettingItemText_titleMaxWidth, 0);
        if (titleMaxWidth > 0) {
            setTitleLayoutMaxWidth(titleMaxWidth);
        }

        CharSequence summary = a.getText(R.styleable.SettingItemText_summary);
        if (summary != null) {
            mSummary.setText(summary);
            mSummary.setVisibility(View.VISIBLE);
        }
        mSubTitle.setText(a.getText(R.styleable.SettingItemText_subTitle));
        mSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.settings_item_sub_title_size));

        // If display arrow icon
        boolean showArrow = a.getBoolean(R.styleable.SettingItemText_show_arrow, true);
        if (!showArrow) {
            mArrowIcon.setVisibility(View.GONE);
            LayoutParams params =  new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.RIGHT_OF, R.id.title_summary_layout);
            params.rightMargin = (int)getResources().getDimension(R.dimen.settings_list_item_gap_right);
            mSubTitle.setLayoutParams(params);
        }
        limitTitleMaxSizeIfNeed();
        // If the item clickable
        boolean clickable = a.getBoolean(R.styleable.SettingItemText_clickable, true);
        if (!clickable) {
            mSubTitle.setTextColor(getResources().getColor(R.color.setting_item_summary_text_color));
            mTitle.setTextColor(getResources().getColor(R.color.setting_item_text_color));
        }
        a.recycle();
    }

    private boolean mPressed;

    @Override
    public void setPressed(boolean pressed) {
        if(!this.isEnabled()) {
            return;
        }
        super.setPressed(pressed);
        if (mPressed != pressed) {
            mPressed = pressed;
            refreshState();
        }
    }

    private void refreshState() {
        if (mArrowDrawable != null) {
            mArrowDrawable.setState(getDrawableState());
            mArrowDrawable.invalidateSelf();
        }
        if (mBadgeDrawable != null && mBadgeDrawable instanceof StateListDrawable) {
            mBadgeDrawable.setState(getDrawableState());
            mBadgeDrawable.invalidateSelf();
        }
    }

    private void setTitleLayoutMaxWidth(int maxWidth) {
        mTitle.setMaxWidth(maxWidth);
        mSummary.setMaxWidth(maxWidth);
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
        Utils.setMaxTextSizeForTextView(mSummary, maxTextSize);
    }

    private float caculateTextWidth(TextView tv) {
        Paint paint = new Paint();
        paint.setTextSize(tv.getTextSize());
        CharSequence text = tv.getText();
        float ret = 0;
        if (text != null) {
            ret = paint.measureText(tv.getText().toString());
        }
        return ret;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (mShowBadge) {
            canvas.save();
            int arrowRightMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_right_arrow_margin);
            int badgeTransX = getWidth() - arrowRightMargin
                    - mArrowIcon.getWidth() - mBadgeDrawable.getIntrinsicWidth();
            mBadgeDrawable.setBounds(0, 0, mBadgeDrawable.getIntrinsicWidth(), mBadgeDrawable.getIntrinsicHeight());
            canvas.translate(badgeTransX, (mSecondaryLayout.getTop() + mSecondaryLayout.getBottom() - mBadgeDrawable
                    .getIntrinsicHeight()) / 2);
            mBadgeDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public ImageView getIconView() {
        return mIcon;
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        setPadding(0, 0, 0, 0);
        setBackground(mContext.getDrawable(resid));
    }

    public void setIconResource(int iconRes) {
        mIcon.setImageResource(iconRes);
        if (mIcon.getVisibility() != View.VISIBLE) {
            mIcon.setVisibility(View.VISIBLE);
            limitTitleMaxSizeIfNeed();
            invalidate();
        }
    }

    public void setIconDrawable(Drawable iconDrawable) {
        mIcon.setImageDrawable(iconDrawable);
        if (mIcon.getVisibility() != View.VISIBLE) {
            mIcon.setVisibility(View.VISIBLE);
            limitTitleMaxSizeIfNeed();
            invalidate();
        }
    }

    public void setIconRightMargin(int dimen) {
        RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIcon.getLayoutParams();
        int lastMargin = iconParams.rightMargin;
        if (lastMargin != dimen) {
            iconParams.rightMargin = dimen;
            limitTitleMaxSizeIfNeed();
            invalidate();
        }
    }

    public CharSequence getTitle() {
        return mTitle.getText();
    }

    public void setTitle(CharSequence aTitle) {
        Utils.resetTextViewFontSizeAttr(getContext(), mTitle, R.dimen.settings_item_title_size);
        mTitle.setText(aTitle);
        limitTitleMaxSizeIfNeed();
    }

    public void setTitle(int titleRes) {
        String title = null;
        if (titleRes > 0) {
            title = getContext().getString(titleRes);
        }
        setTitle(title);
    }

    public TextView getTitleView() {
        return mTitle;
    }

    public TextView getSubTitleView() {
        return mSubTitle;
    }

    public TextView getSummaryView() {
        return mSummary;
    }

    public CharSequence getSubTitle() {
        return mSubTitle.getText();
    }

    public void setSubTitle(CharSequence aSubTitle) {
        mSubTitle.setVisibility(View.VISIBLE);
        mSubTitle.setText(aSubTitle);
        limitTitleMaxSizeIfNeed();
    }

    public void setSubTitle(int subTitleRes) {
        setSubTitle(getResources().getString(subTitleRes));
    }

    public CharSequence getSummary() {
        return mSummary.getText();
    }

    public void setSummary(CharSequence summary) {
        if (!TextUtils.isEmpty(summary)) {
            mSummary.setVisibility(View.VISIBLE);
            LayoutParams params  = (LayoutParams)mTitleSummaryLayout.getLayoutParams();
            params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_title_right_margin);
            mTitleSummaryLayout.setLayoutParams(params);
        } else {
            mSummary.setVisibility(View.GONE);
        }
        mSummary.setText(summary);
        limitTitleMaxSizeIfNeed();
    }

    public void setArrowVisible(boolean isVisible) {
        mArrowIcon.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Deprecated
    public void setItemEnable(boolean enabled) {
        this.setEnabled(enabled);
    }

    /**
     * Note: call this method after subtitle has been set(if contain subtitle)
     */
    public void setBadgeResources(int badgeResId) {
        if (mBadgeDrawable == null || currentBadgeRes != badgeResId) {
            currentBadgeRes = badgeResId;
            try {
                if (badgeResId > 0) {
                    mBadgeDrawable = getResources().getDrawable(badgeResId);
                } else {
                    mBadgeDrawable = null;
                }
            } catch (Exception e) {
                mBadgeDrawable = null;
            }
        }
        setBadgeVisibility(mBadgeDrawable != null);
    }

    private void setBadgeVisibility(boolean visible) {
        mShowBadge = visible;
        invalidate();
    }

    public void limitTitleMaxSizeIfNeed() {
        Utils.resetTextViewFontSizeAttr(getContext(), mTitle, R.dimen.settings_item_title_size);
        int titleMaxWidth = getTitleMaxWidth();
        float requireWidth = Utils.caculateTextWidth(mTitle);
        if (requireWidth > titleMaxWidth || mTitle.getMaxWidth() > 0 && requireWidth > mTitle.getMaxWidth()) {
            setMaxTitleSize(getResources().getDimensionPixelSize(R.dimen.common_max_size));
        }
    }

    private int getTitleMaxWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final int arrowIconWidth = getResources().getDimensionPixelOffset(R.dimen.settings_item_arrow_width);

        int itemLeftMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_title_left_margin);
        int itemRightMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_title_right_margin);
        int arrowRightMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_right_arrow_margin);
        int arrowOccupyWidth = arrowIconWidth + arrowRightMargin;
        int iconOccupyWidth = 0;
        int sublTitleLeftMargin = 0;
        int subTitleWidth = 0;
        if(mIcon.getVisibility() == View.VISIBLE) {
            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIcon.getLayoutParams();
            iconOccupyWidth = getResources().getDimensionPixelOffset(R.dimen.setting_item_icon_width)
                    + iconParams.rightMargin;
        }
        if (mSubTitle.getVisibility() == View.VISIBLE) {
            subTitleWidth = (int) Utils.caculateTextWidth(mSubTitle);
            if (subTitleWidth > 0) {
                sublTitleLeftMargin = getResources().getDimensionPixelOffset(R.dimen
                        .setting_item_text_subtitle_margin_left);
            }
        }
        int badgeWidth = 0;
        if (mShowBadge) {
            badgeWidth = mBadgeDrawable.getIntrinsicWidth();
        }
        int titleMaxWidth = metrics.widthPixels - itemLeftMargin - itemRightMargin - arrowOccupyWidth
                - sublTitleLeftMargin - subTitleWidth - badgeWidth - iconOccupyWidth;
        return titleMaxWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //set subtitle first
        int arrowWidth = mArrowIcon.getWidth();
        if (arrowWidth == 0) {
            arrowWidth = (int) (86 * mDensity / 3);
        }
        if (mShowBadge) {
            if (mSubTitle.getVisibility() == View.VISIBLE && mSubTitle.getText().length() > 0) {
                LayoutParams params = (LayoutParams) mSubTitle.getLayoutParams();//new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.title_summary_layout);
                int arrowRightMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_right_arrow_margin);
                int badgeTransX = getWidth() - arrowRightMargin
                        - arrowWidth - mBadgeDrawable.getIntrinsicWidth();
                params.rightMargin = getWidth() -
                        (badgeTransX - getResources().getDimensionPixelSize(R.dimen.setting_item_text_badge_subtitle_margin));
                mSubTitle.setLayoutParams(params);
            }
        } else {
            if (mSubTitle.getVisibility() == View.VISIBLE && mSubTitle.getText().length() > 0) {
                LayoutParams params = (LayoutParams) mSubTitle.getLayoutParams();//new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.RIGHT_OF, R.id.title_summary_layout);
                if (mArrowIcon.getVisibility() == View.VISIBLE) {
                    int arrowRightMargin = getResources().getDimensionPixelOffset(R.dimen.settings_item_right_arrow_margin);
                    int badgeTransX = getWidth() - arrowRightMargin - arrowWidth;
                    params.rightMargin = getWidth() - badgeTransX;
                } else {
                    params.rightMargin = (int)getResources().getDimension(R.dimen.settings_list_item_gap_right);
                }
                mSubTitle.setLayoutParams(params);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * set string resource id which will display when disabled settings item be clicked
     */
    public void setDisabledTips(int stringId) {
        mDisableReasonStringId = stringId;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() && mDisableReasonStringId > 0) {
            if (mToast == null || mToast.getView().getWindowVisibility() != View.VISIBLE) {
                mToast = Toast.makeText(getContext(), mDisableReasonStringId, Toast.LENGTH_LONG);
                mToast.show();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
