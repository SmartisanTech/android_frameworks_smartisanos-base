package smartisanos.app;

import com.smartisanos.internal.R;
import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import smartisanos.widget.MenuDialogTitleBar;

public class MenuDialog extends Dialog {
    private TextView mCancelButtonRight = null;
    private TextView mCancelButtonLeft = null;
    private TextView mOkBtn;
    private ListView mListView = null;
    private Context mContext;
    private MenuDialogTitleBar mTitleBar;

    public MenuDialog(Context context) {
        super(context, R.style.MenuDialogTheme);
        this.mContext = context;
        init();
    }

    public void setTitle(int titleId) {
        mTitleBar.setTitle(mContext.getText(titleId));
    }

    public void setTitle(CharSequence title) {
        mTitleBar.setTitle(title);
    }

    public void setTitleSinleLine(boolean singleLine) {
        mTitleBar.setTitleSingleLine(singleLine);
    }

    private void init() {
        setContentView(R.layout.menu_dialog);
        mTitleBar = (MenuDialogTitleBar) findViewById(R.id.menu_dialog_title_bar);

        mCancelButtonRight = (TextView) this.findViewById(R.id.btn_cancel_right);
        mCancelButtonRight.setOnClickListener(mCancelListener);
        mCancelButtonLeft = (TextView) this.findViewById(R.id.btn_cancel_left);
        mCancelButtonLeft.setOnClickListener(mCancelListener);
        initLeftRightHands();

        mOkBtn = (TextView) findViewById(R.id.btn_ok);
        mListView = (ListView) this.findViewById(R.id.content_list);
        mListView.setDividerHeight(0);

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().addFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        getWindow().addFlags(LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void initLeftRightHands() {
        int value = 1;
        if (value == 0) {
            mCancelButtonLeft.setVisibility(View.VISIBLE);
            mCancelButtonRight.setVisibility(View.INVISIBLE);
        } else {
            mCancelButtonLeft.setVisibility(View.INVISIBLE);
            mCancelButtonRight.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener mCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public void setAdapter(final MenuDialogListAdapter adapter) {
        mListView.setVisibility(View.VISIBLE);
        mListView.setAdapter(adapter);
        mListView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mListView.setBackgroundResource(R.drawable.menu_dialog_background);
        adapter.setDialog(this);
    }

    public void setAdaper(final MenuDialogMultiAdapter adapter, final OnItemClickListener listener) {
        mListView.setVisibility(View.VISIBLE);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(listener);
        mListView.getLayoutParams().height = (int) mContext.getResources().getDimension(
                R.dimen.menu_dialog_multi_list_height);
        mListView.setBackgroundResource(R.drawable.menu_dialog_multi_list_bg);
    }

    public ListView getListView() {
        return mListView;
    }

    public void setNegativeButton(int textId, View.OnClickListener listener) {
        setNegativeButton(mContext.getText(textId), listener);
    }

    public void setNegativeButton(CharSequence text, View.OnClickListener listener) {
        mTitleBar.setRightButtonText(text);
        mCancelButtonRight.setOnClickListener(listener);
        mTitleBar.setLeftButtonText(text);
        mCancelButtonLeft.setOnClickListener(listener);
    }

    public void setNegativeButton(View.OnClickListener listener) {
        mCancelButtonRight.setOnClickListener(listener);
        mCancelButtonLeft.setOnClickListener(listener);
    }

    /**
     * set the positive button's background is red, for example the positive used
     * to delete. Otherwise, you maybe not need to invoke this method, because the
     * positive button's default background is red.
     * @param isRedBackground
     */
    public void setPositiveRedBg(boolean isRedBackground) {
        if (isRedBackground) {
            mOkBtn.setBackgroundResource(R.drawable.menu_dialog_button_red);
        } else {
            mOkBtn.setBackgroundResource(R.drawable.menu_dialog_button_gray);
        }
    }

    public void setPositiveButton(int textId, View.OnClickListener listener) {
        setPositiveButton(mContext.getText(textId), listener);
    }

    public void setPositiveButton(CharSequence text, final View.OnClickListener listener) {
        mOkBtn.setVisibility(View.VISIBLE);
        mOkBtn.setText(text);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuDialog.this.dismiss();
                listener.onClick(v);
            }
        });
    }

    public void setPositiveButtonGone() {
        mOkBtn.setText(null);
        mOkBtn.setOnClickListener(null);
        mOkBtn.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            initLeftRightHands();
        }
    }
}
