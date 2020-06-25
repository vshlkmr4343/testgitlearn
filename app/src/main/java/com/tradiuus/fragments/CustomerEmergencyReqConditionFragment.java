package com.tradiuus.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tradiuus.R;
import com.tradiuus.activities.CustomerEmergencyCaseActivity;
import com.tradiuus.adapters.ValueArrayAdapter;
import com.tradiuus.models.ConfigData;
import com.tradiuus.models.Option;
import com.tradiuus.models.Question;
import com.tradiuus.models.Service;
import com.tradiuus.models.Trade;
import com.tradiuus.models.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerEmergencyReqConditionFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ListView list_select_type;
    private LinearLayout linear_qsanw;
    private ImageView req_type;
    private TextView title, condition;
    private List<Service> services;
    private DisplayImageOptions options;
    public Trade selectedTrade;
    private int lastSelectedIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_emergency_req_condition, null);
        this.context = getActivity();
        services = ((CustomerEmergencyCaseActivity) getActivity()).services;
        selectedTrade = ((CustomerEmergencyCaseActivity) getActivity()).selectedTrade;
        initView(rootView);
        initViewLoad();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void initView(View rootView) {
        req_type = (ImageView) rootView.findViewById(R.id.req_type);
        list_select_type = (ListView) rootView.findViewById(R.id.list_of_emergencies);
        linear_qsanw = (LinearLayout) rootView.findViewById(R.id.linear_qsanw);
        title = (TextView) rootView.findViewById(R.id.title);
        condition = (TextView) rootView.findViewById(R.id.condition);
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        try {
            ConfigData mConfigData = ((CustomerEmergencyCaseActivity) getActivity()).mConfigData;
            if (mConfigData != null && mConfigData.getService_types() != null) {
                for (Service service : mConfigData.getService_types()) {
                    if (service.getService_type_id().equalsIgnoreCase("1")) {
                        ((TextView) rootView.findViewById(R.id.charges_for_emergency)).setText(service.getContractor_charge());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initViewLoad() {
        try {
            ServiceQuestionsAdapter mServiceQuestionsAdapter = new ServiceQuestionsAdapter(context, services);
            list_select_type.setAdapter(mServiceQuestionsAdapter);
            title.setText("Need A " + selectedTrade.getTrade_name() + " for an Emergency");
            ImageLoader.getInstance().displayImage(selectedTrade.getFeatured_normal_image(), req_type, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    try {
                        DrawableCompat.setTint(DrawableCompat.wrap(((ImageView) view).getDrawable()), Color.parseColor("#5fd8e3"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            try {
                if (lastSelectedIndex >= 0) {
                    populateQsAnsw(services.get(lastSelectedIndex));
                    final ArrayList<Question> questions = services.get(lastSelectedIndex).getService_questions();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < questions.size(); i++) {
                                Question mQuestion = questions.get(i);
                                LinearLayout parent = (LinearLayout) linear_qsanw.getChildAt(i);
                                AppCompatSpinner spn = (AppCompatSpinner) parent.getChildAt(1);
                                if (spn != null && ((CustomerEmergencyCaseActivity) getActivity()).qsList != null && ((CustomerEmergencyCaseActivity) getActivity()).qsList.size() > 0) {
                                    Question mapQuestion = ((CustomerEmergencyCaseActivity) getActivity()).qsList.get(mQuestion.getQuestion_id());
                                    if (mapQuestion != null) {
                                        for (int j = 0; j < mQuestion.getOptions().size(); j++) {
                                            if (mQuestion.getOptions().get(j).getOption_id().equalsIgnoreCase(mapQuestion.getOption_id())) {
                                                spn.setSelection(j);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            condition.setText(((CustomerEmergencyCaseActivity) getActivity()).questionnaire.getQuestion_text());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateQsAnsw(Service service) {
        try {
            linear_qsanw.removeAllViews();
            if (service.isSelected) {
                ((CustomerEmergencyCaseActivity) getActivity()).onServiceItemSelect(service);
                for (int i = 0; i < service.getService_questions().size(); i++) {
                    Question question = service.getService_questions().get(i);
                    LinearLayout rowLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.inflate_qs_answ_row, null);
                    TextView textView = (TextView) rowLayout.getChildAt(0);
                    AppCompatSpinner spn = (AppCompatSpinner) rowLayout.getChildAt(1);
                    textView.setText(question.getQuestion());
                    ArrayList<Value> items = new ArrayList<>();
                    for (Option option : question.getOptions()) {
                        items.add(new Value(option.getOption_id(), option.getOption_value()));
                    }
                    ValueArrayAdapter licenseAdapter = new ValueArrayAdapter(context, items);
                    spn.setAdapter(licenseAdapter);
                    spn.setTag(question);
                    spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                Question mQuestion = (Question) parent.getTag();
                                Value mValue = (Value) parent.getAdapter().getItem(position);
                                mQuestion.setOption_id(mValue.getOption_id());
                                Log.i("onItemSelected", position + "");
                                ((CustomerEmergencyCaseActivity) getActivity()).onQSChoose(mQuestion);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    linear_qsanw.addView(rowLayout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ServiceQuestionsAdapter extends BaseAdapter {

        private List<Service> items;
        private Context context;
        private LayoutInflater inflater;

        public ServiceQuestionsAdapter(Context context, List<Service> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
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
            Service service = items.get(position);
            viewHolder.checkBox.setSelected(service.isSelected);
            viewHolder.txtName.setText(service.getService_name());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastSelectedIndex >= 0) {
                        items.get(lastSelectedIndex).isSelected = false;
                    }
                    items.get(position).isSelected = (items.get(position).isSelected) ? false : true;
                    lastSelectedIndex = position;
                    ((CustomerEmergencyCaseActivity) getActivity()).qsList = new HashMap<>();
                    populateQsAnsw(items.get(position));
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }


        private class ViewHolder {
            public TextView txtName;
            public ImageView checkBox;
        }

    }
}
