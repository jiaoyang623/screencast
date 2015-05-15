package org.daniel.android.dlna;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiaoyang<br>
 *         email: jiaoyang623@qq.com
 * @version 1.0
 * @date May 11 2015 6:52 PM
 */
public class HomeAdapter extends BaseAdapter {
    private List<Device> mList = new ArrayList<Device>();

    public void setData(List<Device> list) {
        mList.clear();
        if (list != null) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Device getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getUUID().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = new TextView(parent.getContext());
            tv.setPadding(10, 10, 10, 10);
            tv.setTextSize(20);
        }

        tv.setText(getItem(position).getFriendlyName());

        return tv;
    }
}
