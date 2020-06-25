package com.tradiuus.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.activities.ContractorProfileUpdateActivity;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ContractorProfileAvailabilityUpdateFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = ContractorProfileAvailabilityUpdateFragment.class.getSimpleName();
    private Context context;
    private TextView edit_count;
    private LinearLayout zip_code_layout;
    private CustomTextView time_from;
    private CustomTextView time_to;
    public Contractor mContractor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_availability, null);
        this.context = getActivity();
        mContractor = ((TradiuusApp) context.getApplicationContext()).mContractor;
        initView(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_btn_update:
                saveEvent();
                break;
            case R.id.btn_posetive:
                calculateTime(true);
                break;
            case R.id.btn_negetive:
                calculateTime(false);
                break;
            case R.id.time_from_edit:
                openTimePicker(time_from);
                break;
            case R.id.time_to_edit:
                openTimePicker(time_to);
                break;
        }
    }


    private void initView(View rootView) {
        ((TextView) rootView.findViewById(R.id.frag_btn_update)).setOnClickListener(this);

        edit_count = (CustomTextView) rootView.findViewById(R.id.edit_count);
        zip_code_layout = (LinearLayout) rootView.findViewById(R.id.zip_code_layout);

        time_from = (CustomTextView) rootView.findViewById(R.id.time_from);
        time_to = (CustomTextView) rootView.findViewById(R.id.time_to);

        ((CustomTextView) rootView.findViewById(R.id.btn_negetive)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.btn_posetive)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.time_from_edit)).setOnClickListener(this);
        ((CustomTextView) rootView.findViewById(R.id.time_to_edit)).setOnClickListener(this);

        try {
            edit_count.setText(mContractor.getAvailable_trucks());
            time_from.setText(mContractor.getBusiness_start_time());
            time_to.setText(mContractor.getBusiness_end_time());
            if (zip_code_layout != null && zip_code_layout.getChildCount() > 0) {
                for (int i = 0; i < mContractor.getZip_codes().size(); i++) {
                    ((TextView) zip_code_layout.getChildAt(i)).setText(mContractor.getZip_codes().get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTime(boolean plus) {
        try {
            String time = edit_count.getText().toString();
            int timeIn = Integer.valueOf(time);
            if (plus) {
                timeIn++;
            } else {
                if (timeIn == 30) {
                    return;
                }
                timeIn--;
            }
            edit_count.setText(timeIn + "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void openTimePicker(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                final Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.MINUTE, selectedMinute);
                mCalendar.set(Calendar.HOUR, selectedHour);
                String time = mCalendar.get(Calendar.HOUR) + "." + mCalendar.get(Calendar.MINUTE) + ((mCalendar.get(Calendar.AM_PM) == Calendar.AM) ? " PM" : " AM");
                textView.setText(time);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void saveEvent() {
        String count = edit_count.getText().toString();
        String from = time_from.getText().toString();
        String to = time_to.getText().toString();

        ArrayList<String> zipCodes = new ArrayList<>();
        if (zip_code_layout != null && zip_code_layout.getChildCount() > 0) {
            for (int i = 0; i < zip_code_layout.getChildCount(); i++) {
                if (!TextUtils.isEmpty(((TextView) zip_code_layout.getChildAt(i)).getText().toString())) {
                    zipCodes.add(((TextView) zip_code_layout.getChildAt(i)).getText().toString());
                }
            }
        }

        if (TextUtils.isEmpty(count)) {
            Utility.uiThreadAlert(context, "Add trucks");
            return;
        }
        if (TextUtils.isEmpty(from)) {
            Utility.uiThreadAlert(context, "Add business start time");
            return;
        }
        if (TextUtils.isEmpty(to)) {
            Utility.uiThreadAlert(context, "Add business end time");
            return;
        }
        if (zipCodes.size() == 0) {
            Utility.uiThreadAlert(context, "Add more zip codes");
            return;
        }
        if (zipCodes.size() == 0) {
            Utility.uiThreadAlert(context, "Add more zip codes");
            return;
        }

        JSONObject availability = new JSONObject();
        try {
            JSONArray zipArray = new JSONArray();
            for (int i = 0; i < zipCodes.size(); i++) {
                zipArray.put(zipCodes.get(i));
            }
            availability.put("available_trucks", count);
            availability.put("business_start_time", from);
            availability.put("business_end_time", to);
            availability.put("zip_codes", zipArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ContractorProfileUpdateActivity) getActivity()).updateProfileData(availability.toString(), "companyBusinessAvailability", ContractorProfileUpdateActivity.FragTag.AREA);
    }
}
