package com.tradiuus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tradiuus.R;
import com.tradiuus.activities.ContractorProfileUpdateActivity;
import com.tradiuus.adapters.ValueArrayAdapter;
import com.tradiuus.helper.UserPreference;
import com.tradiuus.helper.Utility;
import com.tradiuus.models.Contractor;
import com.tradiuus.models.Technician;
import com.tradiuus.models.Value;
import com.tradiuus.widgets.ExpandableHeightListView;
import com.tradiuus.widgets.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.List;

public class ContractorProfileTechnicianUpdateFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = ContractorProfileTechnicianUpdateFragment.class.getSimpleName();
    private Context context;
    private AppCompatSpinner _spn_bcg_check;
    private ExpandableHeightListView list_item;
    private TextView edit_count;
    private Contractor mContractor;
    private ArrayList<Technician> listTechnicians = new ArrayList<>();
    private TradeAdapter tradeAdapter;
    private List<Value> bcgCheckList;
    private String selectedBCGTag = "";
    private int maxCount = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_technician, null);
        this.context = getActivity();
        mContractor = ((ContractorProfileUpdateActivity) getActivity()).contractorImpl.getmContractor();
        initView(rootView);
        initLoad();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_negetive:
                calculateTime(false);
                break;

            case R.id.btn_posetive:
                calculateTime(true);
                break;
        }
    }


    private void initView(View rootView) {
        _spn_bcg_check = (AppCompatSpinner) rootView.findViewById(R.id.bcg_check);
        list_item = (ExpandableHeightListView) rootView.findViewById(R.id.list_item);
        edit_count = (TextView) rootView.findViewById(R.id.edit_count);
        list_item.setExpanded(true);

        (rootView.findViewById(R.id.btn_negetive)).setOnClickListener(this);
        (rootView.findViewById(R.id.btn_posetive)).setOnClickListener(this);
    }

    private void initLoad() {
        listTechnicians = (mContractor.getTechnicians() != null) ? new ArrayList<Technician>(mContractor.getTechnicians()) : new ArrayList<Technician>();
        tradeAdapter = new TradeAdapter(context, listTechnicians);
        list_item.setAdapter(tradeAdapter);
        edit_count.setText(listTechnicians.size() + "");

        bcgCheckList = new ArrayList<>();
        bcgCheckList.add(new Value("0", "Yes"));
        bcgCheckList.add(new Value("1", "No"));

        try {
            ValueArrayAdapter licenseAdapter = new ValueArrayAdapter(context, bcgCheckList);
            selectedBCGTag = TextUtils.isEmpty(mContractor.getBackground_check()) ? "" : mContractor.getBackground_check();
            _spn_bcg_check.setAdapter(licenseAdapter);
            _spn_bcg_check.setSelection(selectedBCGTag.equalsIgnoreCase("Yes") ? 0 : 1, false);
            _spn_bcg_check.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedBCGTag = bcgCheckList.get(position).getOption();
                    mContractor.setBackground_check(selectedBCGTag);
                    UserPreference.saveContractor(context, mContractor);
                    if (position == 0) {
                        Utility.uiThreadAlert(context, "Congrats", "Successfully Updated", "Ok", new Utility.OnDialogButtonClick() {
                            @Override
                            public void onOkayButtonClick() {

                            }
                        });
                    } else {
                        Utility.uiThreadAlert(context, "Waring", "Please note that the technician's public profile will show him as background not checked and may affect customer's selection process", "Ok", new Utility.OnDialogButtonClick() {
                            @Override
                            public void onOkayButtonClick() {

                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTime(boolean plus) {
        try {
            String currentCount = edit_count.getText().toString();
            int count = Integer.valueOf(currentCount);
            if (count > 0 && count < (maxCount + 1)) {
                if (plus) {
                    if (count == maxCount) {
                        return;
                    }
                    count++;
                    try {
                        listTechnicians.add(new Technician("", "", "", "", true, true));
                        tradeAdapter.update(listTechnicians);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (count == 0) {
                        return;
                    }
                    count--;
                    try {
                        if (listTechnicians.get(count) != null) {
                            if (TextUtils.isEmpty(listTechnicians.get(count).getTechnician_id())) {
                                listTechnicians.remove(count);
                                tradeAdapter.update(listTechnicians);
                            } else {
                                ((ContractorProfileUpdateActivity) getActivity()).removeTechnician(listTechnicians.get(count).getTechnician_id());
                                count++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                edit_count.setText(count + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TradeAdapter extends BaseAdapter {

        private List<Technician> items;
        private Context context;
        private LayoutInflater inflater;
        private com.tradiuus.adapters.TradeAdapter.OnItemClickListener onItemClickListener;

        public TradeAdapter(Context context, List<Technician> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public void update(List<Technician> items) {
            this.items = items;
            notifyDataSetChanged();
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
                convertView = inflater.inflate(R.layout.inflate_profile_technician_detail, null);
                viewHolder.layout_details = (LinearLayout) convertView.findViewById(R.id.layout_details);
                viewHolder.technician_no = (TextView) convertView.findViewById(R.id.technician_no);
                viewHolder.btnPosetive = (TextView) convertView.findViewById(R.id.btn_posetive);
                viewHolder.btn_modify = (TextView) convertView.findViewById(R.id.btn_modify);
                viewHolder.btn_remove = (TextView) convertView.findViewById(R.id.btn_remove);
                viewHolder.btn_add = (TextView) convertView.findViewById(R.id.btn_add);
                viewHolder.txtName = (EditText) convertView.findViewById(R.id.edt_technician_name);
                viewHolder.txtPhone = (MaskedEditText) convertView.findViewById(R.id.edt_technician_phno);
                viewHolder.txtEmail = (EditText) convertView.findViewById(R.id.edt_technician_email);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Technician mTechnician = items.get(position);

            if (mTechnician.isNew) {
                viewHolder.btn_modify.setVisibility(View.GONE);
                viewHolder.btn_remove.setVisibility(View.GONE);
                viewHolder.btn_add.setVisibility(View.VISIBLE);
            } else {
                viewHolder.btn_modify.setVisibility(View.VISIBLE);
                viewHolder.btn_remove.setVisibility(View.VISIBLE);
                viewHolder.btn_add.setVisibility(View.GONE);
            }
            viewHolder.layout_details.setVisibility(mTechnician.isExpand ? View.VISIBLE : View.GONE);
            viewHolder.technician_no.setText("Technician: " + (position + 1));
            viewHolder.txtName.setText(mTechnician.getName());
            viewHolder.txtPhone.setText(mTechnician.getMobile());
            viewHolder.txtEmail.setText(mTechnician.getEmail());


            viewHolder.txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        items.get(position).setName(((EditText) v).getText().toString());
                    }
                }
            });

            viewHolder.txtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        items.get(position).setMobile(((EditText) v).getText().toString());
                    }
                }
            });

            viewHolder.txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        items.get(position).setEmail(((EditText) v).getText().toString());
                    }
                }
            });

            viewHolder.btn_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ContractorProfileUpdateActivity) getActivity()).technicianAddUpdate(new Gson().toJson(items.get(position)).toString(), "updateTechnicians", false);
                }
            });
            viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ContractorProfileUpdateActivity) getActivity()).removeTechnician(items.get(position).getTechnician_id());
                }
            });
            viewHolder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ContractorProfileUpdateActivity) getActivity()).technicianAddUpdate(new Gson().toJson(items.get(position)).toString(), "updateTechnicians", true);
                }
            });

            viewHolder.btnPosetive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.get(position).isExpand = (items.get(position).isExpand) ? false : true;
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


        private class ViewHolder {
            public LinearLayout layout_details;
            public TextView technician_no;
            public TextView btnPosetive;
            public TextView btn_modify;
            public TextView btn_remove;
            public TextView btn_add;
            public EditText txtName;
            public MaskedEditText txtPhone;
            public EditText txtEmail;
        }

    }
}
