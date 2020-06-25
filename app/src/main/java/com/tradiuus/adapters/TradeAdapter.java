package com.tradiuus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.models.Trade;
import com.tradiuus.models.Value;

import java.util.List;

public class TradeAdapter extends BaseAdapter {

    private List<Trade> items;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private int lastSelectedIndex = -1;

    public TradeAdapter(Context context, List<Trade> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.onItemClickListener = onItemClickListener;
    }

    public TradeAdapter(Context context, List<Trade> items, int lastSelectedIndex, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.lastSelectedIndex = lastSelectedIndex;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.inflate_trade_row, null);
            viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.trade_row_radio);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.trade_row_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Trade trade = items.get(position);
        viewHolder.checkBox.setSelected((trade.isSelected == 1) ? true : false);
        viewHolder.txtName.setText(trade.getTrade_name());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedIndex == position) {
                    items.get(lastSelectedIndex).isSelected = 0;
                    lastSelectedIndex = -1;
                } else {
                    if (lastSelectedIndex > -1) {
                        items.get(lastSelectedIndex).isSelected = 0;
                    }
                    items.get(position).isSelected = 1;
                    lastSelectedIndex = position;
                }

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick((items.get(position).isSelected == 1) ? items.get(position) : null);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    private class ViewHolder {
        public TextView txtName;
        public ImageView checkBox;
    }

    public interface OnItemClickListener {
        void onItemClick(Trade trade);
    }
}