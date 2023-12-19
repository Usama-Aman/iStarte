package com.minbio.erp.financial_management.accounting.fragments.personalized_group;

import android.app.Dialog;
import android.content.Context;
import android.icu.text.MessagePattern;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.minbio.erp.R;
import com.minbio.erp.customer_management.models.ComplaintItems;
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedAccountData;
import com.minbio.erp.financial_management.model.ParentAccountsData;

import java.util.ArrayList;

public class MultiAccountSpinner extends Dialog {

    private ArrayList<PersonalizedAccountData> filteredList = new ArrayList<>();
    private ArrayList<PersonalizedAccountData> unFilteredList = new ArrayList<>();
    private ListView workflowListView;
    private TextView dialogTitle;
    private EditText editTextSearch;
    private Button btnCloseSpinnerDialog, btnDoneSpinnerDialog, btnSelectAllSpinnerDialog;
    private Context context;
    private OnMultiSelectDoneListener onMultiSelectDoneListener;
    private boolean isDoneHit = false;
    private ArrayList<Integer> result = new ArrayList<Integer>();

    private ArrayList<Integer> trueIds = new ArrayList<Integer>();

    public MultiAccountSpinner(@NonNull Context context, ArrayList<PersonalizedAccountData> strings) {
        super(context);
        this.context = context;
        this.filteredList = strings;
        this.unFilteredList = strings;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_list_layout);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        dialogTitle = findViewById(R.id.dilog_list_title);
        workflowListView = findViewById(R.id.listview_dialog);
        editTextSearch = findViewById(R.id.editTextSpinnerDialog);
        btnCloseSpinnerDialog = findViewById(R.id.btnCloseSpinnerDialog);
        btnDoneSpinnerDialog = findViewById(R.id.btnDoneSpinnerDialog);
        btnSelectAllSpinnerDialog = findViewById(R.id.btnSelectAllSpinnerDialog);

        dialogTitle.setText(context.getResources().getString(R.string.cmCustComplaintLabelSelectItem));

        ListAdapter listAdapter = new ListAdapter(context, unFilteredList);
        workflowListView.setAdapter(listAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                listAdapter.getFilter().filter(s.toString());
            }
        });

        btnCloseSpinnerDialog.setOnClickListener(v -> {
            dismiss();
        });

        btnDoneSpinnerDialog.setOnClickListener(v -> {
            for (int i = 0; i < filteredList.size(); i++) {
                if (filteredList.get(i).isChecked())
                    trueIds.add(filteredList.get(i).getId());
            }
            onMultiSelectDoneListener.onMultiSelectDone(trueIds);
            dismiss();
        });

        btnSelectAllSpinnerDialog.setOnClickListener(v -> {
            editTextSearch.setText("");
            for (int i = 0; i < filteredList.size(); i++)
                filteredList.get(i).setChecked(true);
            listAdapter.notifyDataSetChanged();
        });

    }

    private class ListAdapter extends BaseAdapter implements Filterable {
        private LayoutInflater mInflater;


        public ListAdapter(Context c, ArrayList<PersonalizedAccountData> list) {
            context = c;
            filteredList = list;
            unFilteredList = list;
            this.mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListAdapter.ViewHolder holder;
            try {
                if (convertView == null) {
                    holder = new ListAdapter.ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_multiselect_dialog_layout, null);
                    holder.name = convertView.findViewById(R.id.multiSelectName);
                    holder.checkBox = convertView.findViewById(R.id.multiSelectCheckBox);
                    holder.constraintLayout = convertView.findViewById(R.id.constraintItemMultiSelect);
                    convertView.setTag(holder);

                } else {
                    holder = (ListAdapter.ViewHolder) convertView.getTag();
                }
                holder.name.setText(filteredList.get(position).getLabel());

                holder.checkBox.setChecked(filteredList.get(position).isChecked());

                holder.constraintLayout.setOnClickListener(v -> {
                    if (filteredList.get(position).isChecked()) {
                        filteredList.get(position).setChecked(false);
                        holder.checkBox.setChecked(false);
                    } else {
                        filteredList.get(position).setChecked(true);
                        holder.checkBox.setChecked(true);
                    }

                });

            } catch (
                    Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count == 0) {
                        notifyDataSetInvalidated();
                    } else {

                        filteredList = (ArrayList<PersonalizedAccountData>) results.values;
                        notifyDataSetChanged();
                    }
                }

                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filteredList = unFilteredList;
                    } else {
                        ArrayList<PersonalizedAccountData> fl = new ArrayList<>();
                        for (PersonalizedAccountData row : unFilteredList) {
                            if (row.getLabel().toLowerCase().startsWith(charString.toLowerCase())) {
                                fl.add(row);
                            }
                        }

                        filteredList = fl;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;
                    return filterResults;

                }
            };

            return filter;
        }

        private class ViewHolder {
            TextView name;
            CheckBox checkBox;
            ConstraintLayout constraintLayout;
        }
    }

    public void setOnworkflowlistclicklistener(OnMultiSelectDoneListener listener) {
        this.onMultiSelectDoneListener = listener;
    }

    public interface OnMultiSelectDoneListener {
        void onMultiSelectDone(ArrayList<Integer> results);
    }


}
