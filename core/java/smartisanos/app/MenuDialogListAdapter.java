package smartisanos.app;

import com.smartisanos.internal.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * this adapter used to menu dialog
 * and list item is text view
 *
 */
public class MenuDialogListAdapter extends BaseAdapter {
    private Context context;
    private Dialog dialog;
    private List<String> list = new ArrayList<String>();
    private List<OnClickListener> listener = new ArrayList<OnClickListener>();
    private boolean mHasRecentCall = false;

    public MenuDialogListAdapter(Context context, List<String> list, List<OnClickListener> listener) {
        this(context, list, listener, false);
    }

    public MenuDialogListAdapter(Context context, List<String> list, List<OnClickListener> listener, boolean hasRecentCall) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        if (list == null || listener == null || list.size() != listener.size()) {
            throw new IllegalArgumentException();
        }
        mHasRecentCall = hasRecentCall;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int dipToPx(double dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_dialog_list_item, null);
        }
        if (mHasRecentCall && position == 0) {
            convertView.setBackgroundResource(R.drawable.recent_call_item_selector);
            int padding = context.getResources().getDimensionPixelSize(R.dimen.recent_call_padding_left);
            convertView.setPadding(padding, 0, padding, 0);
        } else {
            convertView.setBackgroundResource(R.drawable.menu_dialog_button_gray);
        }

        final TextView button = (TextView) convertView;
        button.setText(list.get(position));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                listener.get(position).onClick(button);
            }
        });
        return convertView;
    }
}
