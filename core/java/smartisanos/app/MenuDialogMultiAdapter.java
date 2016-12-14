
package smartisanos.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import com.smartisanos.internal.R;

import smartisanos.widget.FontFitTextView;

public class MenuDialogMultiAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<String> mList;
    private boolean mHasRecentCall;
    private Context mContext;

    public MenuDialogMultiAdapter(Context context, List<String> list) {
        this(context, list, false);
        mContext = context;
    }

    public MenuDialogMultiAdapter(Context context, List<String> list, boolean hasRecentCall) {
        if (list == null) {
            throw new IllegalArgumentException();
        }

        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
        mHasRecentCall = hasRecentCall;
    }

    public int getCount() {
        return mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private int dipToPx(double dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.menu_dialog_list_multi_item, null);
        }

        if (position == getCount() - 1) {
            convertView.setBackgroundResource(R.drawable.menu_dialog_last_item_selector);
        } else if (mHasRecentCall && position == 0) {
            convertView.setBackgroundResource(R.drawable.recent_call_multi_item_selector);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.recent_call_padding);
            convertView.setPadding(padding, 0, padding, 0);
        } else {
            convertView.setBackgroundResource(R.drawable.menu_dialog_multi_item_selector);
        }

        final FontFitTextView button = (FontFitTextView) convertView;
        button.setText(mList.get(position));
        button.setMinTextSize(button.getResources().getDimension(R.dimen.menu_dialog_item_text_min_size));

        return convertView;
    }
}
