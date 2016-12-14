
package smartisanos.widget;

import com.smartisanos.internal.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @hide
 */
public class SwitchEx extends CheckBox {


    private final float VELOCITY = 350;
    private final float EXTENDED_OFFSET_Y = 2;
    private final float ANIMATION_FRAME_DURATION = 1000 / 60;

    private Resources mResources;
    private ViewParent mParent;

    private  boolean isClean;

    private  final int MAX_ALPHA = 255;
    private  final int MIN_ALPHA = MAX_ALPHA * 3 / 4; // 3/4 of MAX_ALPHA
    private  Bitmap sBottom;
    private  Bitmap sCurBtnPic;
    private  Bitmap sBtnNormal;
    private  Bitmap sFrame;
    private  Bitmap sMask;
    private  PorterDuffXfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private  Canvas sCanvas = new Canvas();
    private  Paint sPaint = new Paint();
    private  Bitmap sAlphaOnBitmap;
    private  Bitmap sAlphaOffBitmap;
    private  Bitmap sOnBitmap;
    private  Bitmap sOffBitmap;
    private  float sBtnOnPos;
    private  float sBtnOffPos;
    private  float sMaskWidth;
    private  float sMaskHeight;
    private  float sBtnWidth;

    private float mFirstDownY;
    private float mFirstDownX;
    private float mRealPos;
    private float mBtnPos;
    private float mBtnInitPos;
    private float mVelocity;
    private float mExtendOffsetY; // Y轴方向扩大的区域,增大点击区域
    private float mAnimationPosition;

    private float mAnimatedVelocity;
    private int mClickTimeout;
    private int mTouchSlop;
    private int mAlpha = MAX_ALPHA;
    private boolean mChecked = false;
    private boolean mBroadcasting;
    private boolean mTurningOn;
    private boolean mAnimating;
    private boolean mTouching;

    private PerformClick mPerformClick;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    boolean mRestoring = false;

    private int mBottomResId;

    private boolean isScrolling = false;

    private final int[] sDrawableIds = {
        R.drawable.switch_ex_bottom,
        R.drawable.switch_ex_unpressed,
        R.drawable.switch_ex_frame,
        R.drawable.switch_ex_mask
    };

    public static final int SWITCH_BOTTOM    = 0;
    public static final int SWITCH_UNPRESSED = 1;
    public static final int SWITCH_FRAME     = 2;
    public static final int SWITCH_MASK      = 3;

    private static final int HANDLER_MESSAGE_SET_CHECKED = 1;
    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            if (msg.what == HANDLER_MESSAGE_SET_CHECKED) {
                boolean checked = (Boolean)msg.obj;
                setChecked(checked, false);
            }
        };
    };

    public SwitchEx(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public SwitchEx(Context context) {
        this(context, null);
    }

    public void initSwitchBitmap(Resources r) {
        if (sBottom == null || sBtnNormal == null || sFrame == null || sMask == null) {
            sBottom = ((BitmapDrawable)r.getDrawable(R.drawable.switch_ex_bottom)).getBitmap();
            sBtnNormal =((BitmapDrawable)r.getDrawable(R.drawable.switch_ex_unpressed)).getBitmap();
            sFrame = ((BitmapDrawable)r.getDrawable(R.drawable.switch_ex_frame)).getBitmap();
            sMask = ((BitmapDrawable)r.getDrawable(R.drawable.switch_ex_mask)).getBitmap();
            sCurBtnPic = sBtnNormal;
        }
        sPaint.setColor(Color.WHITE);
        sBtnWidth = sBtnNormal.getWidth();
        sMaskWidth = sMask.getWidth();
        sMaskHeight = sMask.getHeight();
        sBtnOnPos = sBtnWidth / 2;
        sBtnOffPos = sMaskWidth - sBtnWidth / 2;
        initBitmap();
    }

    public void clearSwitchBitmap() {
        sBottom = null;
        sBtnNormal = null;
        sFrame = null;
        sMask = null;

        sAlphaOnBitmap = null;
        sAlphaOffBitmap = null;
        sOnBitmap = null;
        sOffBitmap = null;
        isClean = true;
    }

    public SwitchEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (mResources == null) {
            mResources = context.getResources();
        }
        initSwitchBitmap(mResources);
        initView(context);
    }

    private void initView(Context context) {
        mClickTimeout = ViewConfiguration.getPressedStateDuration()
                + ViewConfiguration.getTapTimeout();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mBtnPos = mChecked ? sBtnOnPos : sBtnOffPos;
        mRealPos = getRealPos(mBtnPos);
        final float density = getResources().getDisplayMetrics().density;
        mVelocity = (int) (VELOCITY * density + 0.5f);
        mExtendOffsetY = (int) (EXTENDED_OFFSET_Y * density + 0.5f);
    }

    private  void initBitmap() {
        isClean = false;
        if (sAlphaOnBitmap == null || sAlphaOffBitmap == null
            || sOnBitmap == null || sOffBitmap == null) {
            sAlphaOnBitmap  = createBitmap(MIN_ALPHA, getRealPos(sBtnOnPos));
            sAlphaOffBitmap = createBitmap(MIN_ALPHA, getRealPos(sBtnOffPos));
            sOnBitmap = createBitmap(MAX_ALPHA, getRealPos(sBtnOnPos));
            sOffBitmap = createBitmap(MAX_ALPHA, getRealPos(sBtnOffPos));
        }
    }

    public void setSwitchDrawable(int type, Bitmap bitmap) {
        if (type >= sDrawableIds.length) {
            return;
        }

        if (null == bitmap) {
            return;
        }

        switch(type) {
            case SWITCH_BOTTOM:
                sBottom = bitmap;
                resetBitmaps();
                break;
            case SWITCH_UNPRESSED:
                sBtnNormal = bitmap;
                sCurBtnPic = sBtnNormal;
                resetBitmaps();
                break;
            case SWITCH_FRAME:
                sFrame = bitmap;
                resetBitmaps();
                break;
            case SWITCH_MASK:
                sMask = bitmap;
                resetBitmaps();
                break;
            default:
                break;
        }
    }

    public void setBottomDrawable(int resId) {
        if (mBottomResId == resId) {
            return;
        }
        mBottomResId = resId;
        sBottom = ((BitmapDrawable)mResources.getDrawable(resId)).getBitmap();
        resetBitmaps();
    }

    public void setFrameDrawable(int resId) {
        if (resId == -1) {
            sFrame = ((BitmapDrawable) mResources.getDrawable(R.drawable.switch_ex_frame)).getBitmap();
        } else {
            sFrame = ((BitmapDrawable) mResources.getDrawable(resId)).getBitmap();
        }
        resetBitmaps();
    }

    private void resetBitmaps() {
        sAlphaOnBitmap = null;
        sAlphaOffBitmap = null;
        sOnBitmap = null;
        sOffBitmap = null;
        initBitmap();
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        mAlpha = enabled ? MAX_ALPHA : MIN_ALPHA;
        super.setEnabled(enabled);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    private void setCheckedDelayed(boolean checked) {
        mHandler.removeMessages(HANDLER_MESSAGE_SET_CHECKED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(HANDLER_MESSAGE_SET_CHECKED, checked), 20);
    }

    public void setChecked(boolean checked) {
        setChecked(checked, true);
    }

    private void setChecked(boolean checked, boolean needInvalidate) {
        if (mChecked != checked) {
            mChecked = checked;
            mBtnPos = checked ? sBtnOnPos : sBtnOffPos;
            mRealPos = getRealPos(mBtnPos);
            if (needInvalidate) {
                invalidate();
            }
            // Avoid infinite recursions if setChecked() is called from a
            // listener
            if (mBroadcasting || mRestoring) {
                return;
            }
            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(SwitchEx.this, mChecked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(SwitchEx.this, mChecked);
            }
            mBroadcasting = false;
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        mRestoring = true;
        super.onRestoreInstanceState(state);
        mRestoring = false;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener the callback to call on checked state change
     * @hide
     */
    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAnimating) {
            return true;
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        float deltaX = Math.abs(x - mFirstDownX);
        float deltaY = Math.abs(y - mFirstDownY);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                attemptClaimDrag();
                mFirstDownX = x;
                mFirstDownY = y;
                mBtnInitPos = mChecked ? sBtnOnPos : sBtnOffPos;
                break;
            case MotionEvent.ACTION_MOVE:
                isScrolling = true;
                float time = event.getEventTime() - event.getDownTime();
                mBtnPos = mBtnInitPos + event.getX() - mFirstDownX;
                if (mBtnPos >= sBtnOnPos) {
                    mBtnPos = sBtnOnPos;
                }
                if (mBtnPos <= sBtnOffPos) {
                    mBtnPos = sBtnOffPos;
                }
                mTurningOn = mBtnPos > (sBtnOnPos - sBtnOffPos) / 2 + sBtnOffPos;
                mRealPos = getRealPos(mBtnPos);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isScrolling = false;
                time = event.getEventTime() - event.getDownTime();
                if (deltaY < mTouchSlop && deltaX < mTouchSlop && time < mClickTimeout) {
                    if (mPerformClick == null) {
                        mPerformClick = new PerformClick();
                    }
                    if (!post(mPerformClick)) {
                        performClick();
                    }
                } else {
                    startAnimation(mTurningOn);
                    mTouching = true;
                    invalidate();
                    return true;
                }
                break;
        }
        mTouching = true;
        invalidate();
        return isEnabled();
    }

    private final class PerformClick implements Runnable {
        public void run() {
            performClick();
        }
    }

    @Override
    public boolean performClick() {
        startAnimation(!mChecked);
        return true;
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {
        mParent = getParent();
        if (mParent != null) {
            mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private float getRealPos(float btnPos) {
        return btnPos - sBtnWidth / 2;
    }

    private Bitmap createBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(sMask.getWidth(), sMask.getHeight(), Bitmap.Config.ARGB_8888);
        sCanvas.setBitmap(bitmap);
        sPaint.setAlpha(mAlpha);
        sCanvas.drawBitmap(sMask, 0, 0, sPaint);
        sPaint.setXfermode(sXfermode);
        sCanvas.drawBitmap(sBottom, mRealPos, 0, sPaint);
        sPaint.setXfermode(null);
        sCanvas.drawBitmap(sFrame, 0, 0, sPaint);
        sCanvas.drawBitmap(sCurBtnPic, mRealPos, 0, sPaint);

        return bitmap;
    }

    private  Bitmap createBitmap(int alpha, float pos) {
        Bitmap bitmap = Bitmap.createBitmap(sMask.getWidth(), sMask.getHeight(), Bitmap.Config.ARGB_8888);
        sCanvas.setBitmap(bitmap);
        sPaint.setAlpha(alpha);
        sCanvas.drawBitmap(sMask, 0, 0, sPaint);
        sPaint.setXfermode(sXfermode);
        sCanvas.drawBitmap(sBottom, pos, 0, sPaint);
        sPaint.setXfermode(null);
        sCanvas.drawBitmap(sFrame, 0, 0, sPaint);
        sCanvas.drawBitmap(sCurBtnPic, pos, 0, sPaint);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isClean) {
            initSwitchBitmap(mResources);
        }
        canvas.save();
        Bitmap bitmap;
        final float onPos = getRealPos(sBtnOnPos);
        final float offPos = getRealPos(sBtnOffPos);
        if (mAlpha == MAX_ALPHA) {
            if (mRealPos == offPos) {
                bitmap = sOffBitmap;
            } else if (mRealPos == onPos) {
                bitmap = sOnBitmap;
            } else {
                bitmap = createBitmap();
            }
        } else {
            if (mRealPos == offPos) {
                bitmap = sAlphaOffBitmap;
            } else if (mRealPos == onPos) {
                bitmap = sAlphaOnBitmap;
            } else {
                bitmap = createBitmap();
            }
        }
        sPaint.setAlpha(mAlpha);
        canvas.drawBitmap(bitmap, 0, mExtendOffsetY, sPaint);
        canvas.restore();
        if (mRealPos <= offPos) {
            stopAnimation();
            mAnimationPosition = sBtnOffPos;
            if (mTouching) {
                setCheckedDelayed(false);
                mTouching = false;
            }
        } else if (mRealPos >= onPos) {
            stopAnimation();
            mAnimationPosition = sBtnOnPos;
            if (mTouching) {
                setCheckedDelayed(true);
                mTouching = false;
            }
        } else if (!isScrolling) {
            mAnimating = true;
            doAnimation();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) sMaskWidth, (int) (sMaskHeight + 2 * mExtendOffsetY));
    }

    private void startAnimation(boolean turnOn) {
        mAnimatedVelocity = turnOn ? mVelocity : -mVelocity;
        mAnimationPosition = mBtnPos;
        doAnimation();
    }

    private void stopAnimation() {
        mAnimating = false;
    }

    private void doAnimation() {
        mAnimationPosition += mAnimatedVelocity * ANIMATION_FRAME_DURATION / 1000;
        if (mAnimationPosition <= sBtnOffPos) {
            stopAnimation();
            mAnimationPosition = sBtnOffPos;
            setCheckedDelayed(false);
        } else if (mAnimationPosition >= sBtnOnPos) {
            stopAnimation();
            mAnimationPosition = sBtnOnPos;
            setCheckedDelayed(true);
        }
        moveView(mAnimationPosition);
    }

    private void moveView(float position) {
        mBtnPos = position;
        mRealPos = getRealPos(mBtnPos);
        invalidate();
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(SwitchEx.class.getName());
        event.setChecked(mChecked);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(SwitchEx.class.getName());
        info.setChecked(mChecked);
    }
}
