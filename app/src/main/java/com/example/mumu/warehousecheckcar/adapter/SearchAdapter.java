package com.example.mumu.warehousecheckcar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/***
 *created by 
 *on 2020/9/1
 */
public class SearchAdapter extends BaseAdapter implements Filterable {

    private final Object mLock = new Object();
    private List<String> mObjects;
    private List<String> items;
    private int mResource;
    private int mFieldId = 0;
    private ArrayFilter mFilter;
    private LayoutInflater mInflater;

    /**
     * 支持多音字
     */
    public SearchAdapter(Context context, int textViewResourceId) {
        init(context, textViewResourceId, 0);
        mObjects = new ArrayList<>();
        items = new ArrayList<>();
    }

    private void init(Context context, int resource, int textViewResourceId) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        mFieldId = textViewResourceId;
    }

    public void updataList(List<String> objects) {
        mObjects = objects;
        notifyDataSetChanged();
    }

    public void updataList(String[] objects) {
        ArrayList<String> ts = new ArrayList<>();
        for (String t : objects)
            ts.add(t);
        updataList(ts);
    }

    public int getCount() {
        return mObjects.size();
    }

    public String getItem(int position) {
        return mObjects.get(position);
    }

    public int getPosition(String item) {
        return mObjects.indexOf(item);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view;
        TextView text;
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        try {
            if (mFieldId == 0) {
                text = (TextView) view;
            } else {
                text = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
        }
        text.setText(getItem(position).toString());
        return view;
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (items == null) {
                synchronized (mLock) {
                    items = new ArrayList<>(mObjects);
                }
            }
            int count = items.size();
            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String value = items.get(i);
                if (null != value && null != prefix
                        && value.toLowerCase().contains(prefix.toString().toLowerCase())) {
                    values.add(value);
                }
            }
            results.values = values;
            results.count = values.size();
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            items = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}