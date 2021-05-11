package com.patilparag96.cowinhelper.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapAdapter<K,V> extends BaseAdapter {
    private final List<Map.Entry<K,V>> data;
    private final LayoutInflater mInflater;
    private final int mResource;


    public MapAdapter(Context context, int resource, Map<K, V> map) {
        this.data = new ArrayList<>(map.entrySet());
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map.Entry<K,V> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(android.R.id.text1)).setText((getItem(position).getValue().toString()));

        return view;
    }
}
