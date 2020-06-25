package com.tradiuus.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradiuus.R;
import com.tradiuus.helper.Utility;
import com.tradiuus.network.postparams.ServiceParam;

import java.util.List;

public class EmergencyServiceAdapter extends BaseAdapter {

    public List<ServiceParam> items;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private int lastSelectedIndex = -1;

    public EmergencyServiceAdapter(Context context, List<ServiceParam> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public EmergencyServiceAdapter(Context context, List<ServiceParam> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.inflate_services_table_row, null);
            viewHolder.service_checkbox = (ImageView) convertView.findViewById(R.id.service_checkbox);
            viewHolder.service_name = (TextView) convertView.findViewById(R.id.service_name);
            viewHolder.service_cost = (TextView) convertView.findViewById(R.id.service_cost);
            viewHolder.service_time = (TextView) convertView.findViewById(R.id.service_time);
            viewHolder.service_cost_edit = (TextView) convertView.findViewById(R.id.service_cost_edit);
            viewHolder.service_time_edit = (TextView) convertView.findViewById(R.id.service_time_edit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ServiceParam mServiceParam = items.get(position);
        long time = Long.valueOf(mServiceParam.getJob_time());
        int timeH = (int) (time / 60);
        int timeM = (int) (time % 60);

        viewHolder.service_checkbox.setSelected((mServiceParam.isSelected == 1) ? true : false);
        viewHolder.service_name.setText(mServiceParam.getService_name());
        viewHolder.service_cost.setText("$" + mServiceParam.getPrice_max() + " - $" + mServiceParam.getPrice_min());
        viewHolder.service_time.setText(timeH + "h " + timeM + "m");
        viewHolder.service_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).isSelected = (items.get(position).isSelected == 0) ? 1 : 0;
                notifyDataSetChanged();
            }
        });
        viewHolder.service_cost_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.get(position).isSelected == 0) {
                    Utility.showToast(context, "Please select some item before edit");
                    return;
                }
                loadPriceDialog(items.get(position), position);
            }
        });
        viewHolder.service_time_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.get(position).isSelected == 0) {
                    Utility.showToast(context, "Please select some item before edit");
                    return;
                }
                loadTimeDialog(items.get(position), position);
            }
        });
        return convertView;
    }


    private class ViewHolder {
        public ImageView service_checkbox;
        public TextView service_name;
        public TextView service_cost;
        public TextView service_time;
        public TextView service_cost_edit;
        public TextView service_time_edit;
    }

    public interface OnItemClickListener {
        void onItemClick(ServiceParam mServiceParam);
    }

    private void loadPriceDialog(ServiceParam serviceParam, final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.inflate_dialog_change_price);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText _editPriceMax = (EditText) dialog.findViewById(R.id.dialog_change_editpriceMax);
        final EditText _editPriceMin = (EditText) dialog.findViewById(R.id.dialog_change_editpriceMin);
        try {
            if (serviceParam != null && !TextUtils.isEmpty(serviceParam.getPrice_max())) {
                _editPriceMax.setText(serviceParam.getPrice_max());
            }
            if (serviceParam != null && !TextUtils.isEmpty(serviceParam.getPrice_min())) {
                _editPriceMin.setText(serviceParam.getPrice_min());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((TextView) dialog.findViewById(R.id.dialog_change_editprice_title)).setText("Replacement of " + serviceParam.getService_name() + " Panel");

        ((TextView) dialog.findViewById(R.id.frag_profile_pic_btn_skip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.frag_profile_pic_btn_proceed)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxValue = _editPriceMax.getText().toString();
                String minValue = _editPriceMin.getText().toString();
                if (TextUtils.isEmpty(maxValue) || TextUtils.isEmpty(minValue)) {
                    Utility.showToast(context, "Please provide Max and Min both values");
                    return;
                }
                dialog.dismiss();
                try {
                    items.get(position).setPrice_max(maxValue);
                    items.get(position).setPrice_min(minValue);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void loadTimeDialog(ServiceParam serviceParam, final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.inflate_dialog_change_time);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText _editTimeMax = (EditText) dialog.findViewById(R.id.dialog_change_editTimeMax);
        final EditText _editTimeMin = (EditText) dialog.findViewById(R.id.dialog_change_editTimeMin);
        try {
            if (serviceParam != null && !TextUtils.isEmpty(serviceParam.getJob_time())) {
                int time = Integer.valueOf(serviceParam.getJob_time());
                int hr = (time / 60);
                int min = (time % 60);
                _editTimeMax.setText(String.valueOf(hr));
                _editTimeMin.setText(String.valueOf(min));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((TextView) dialog.findViewById(R.id.dialog_change_editprice_title)).setText("Replacement of " + serviceParam.getService_name() + " Panel");

        ((TextView) dialog.findViewById(R.id.frag_profile_pic_btn_skip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.frag_profile_pic_btn_proceed)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxValue = _editTimeMax.getText().toString();
                String minValue = _editTimeMin.getText().toString();
                if (TextUtils.isEmpty(maxValue) || TextUtils.isEmpty(minValue)) {
                    Utility.showToast(context, "Please provide Max and Min both values");
                    return;
                }
                dialog.dismiss();
                try {
                    items.get(position).setJob_time(String.valueOf(Integer.valueOf(maxValue) * 60 + Integer.valueOf(minValue)));
                    notifyDataSetChanged();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }
}