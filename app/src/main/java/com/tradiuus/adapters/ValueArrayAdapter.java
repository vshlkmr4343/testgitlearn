package com.tradiuus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.models.Value;

import java.util.List;

public class ValueArrayAdapter extends ArrayAdapter<Value> {

    private List<Value> items;
    private Context context;
    private boolean needTextSizeHandle = false;
    private boolean sorterText = false;

    public ValueArrayAdapter(Context context, List<Value> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.items = items;
        this.context = context;
    }

    public ValueArrayAdapter(Context context, List<Value> items, boolean needTextSizeHandle) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.items = items;
        this.context = context;
        this.needTextSizeHandle = needTextSizeHandle;
    }

    public void setSorterText(boolean sorterText) {
        this.sorterText = sorterText;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        if (v == null) {
            v = new TextView(context);
        }
        v.setTextColor(Color.BLACK);
        v.setText(items.get(position).getOption());
        return v;
    }

    @Override
    public Value getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
        if (this.needTextSizeHandle) {
            textView.setSingleLine(false);
            textView.setTextSize(10);
        }
        try {
            if (this.sorterText) {
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView.setText(items.get(position).getOption());
        return textView;
    }
}