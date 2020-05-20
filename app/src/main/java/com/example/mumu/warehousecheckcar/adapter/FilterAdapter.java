package com.example.mumu.warehousecheckcar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.mumu.warehousecheckcar.R;

import java.util.ArrayList;
import java.util.List;

/***
 *created by 模糊查询输入框Adapter
 *on 2020/5/19
 */
public class FilterAdapter extends BaseAdapter implements Filterable {
    private final Object mLock = new Object();
    private Context mContext;
    private List<String> mItems;
    private List<String> fData;
    private MyFilter mFilter;

    public FilterAdapter(Context context) {
        this.mContext = context;
        mFilter = new MyFilter();
        mItems = new ArrayList<>();
        fData = new ArrayList<>();
    }

    public void transforData(List<String> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public void clearData() {
        mItems.clear();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fuzzy_query_item, parent, false);
            viewHolder.content = convertView.findViewById(R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.content.setText(mItems.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    class ViewHolder {
        TextView content;
    }

    class MyFilter extends Filter {
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (fData == null) {
                synchronized (mLock) {
                    fData = new ArrayList<>(mItems);
                }
            }
            int count = fData.size();
            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String value = fData.get(i);
                if (null != value && null != constraint
                        && value.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    values.add(value);
                }
            }
            results.values = values;
            results.count = values.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults results) {
            mItems = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
