package com.razikallayi.suraksha.account;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 13-04-2016.
 */
public class PendingDepositAdapter extends RecyclerView.Adapter<PendingDepositAdapter.ViewHolder> {

    private List<Calendar> mCalendars;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mtvAmount;
        public CheckBox mcbxMonth;

        public ViewHolder(View v) {
            super(v);
            mcbxMonth = (CheckBox) v.findViewById(R.id.chkPendingDepositMonth);
            mtvAmount = (TextView) v.findViewById(R.id.tvPendingDepositsAmount);
            // TODO: 18-05-2016 check if account holder payed for the month. if yes make the checkbox disabled
            mcbxMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {

                    new AlertDialog.Builder(buttonView.getContext())
                            .setMessage("Are you sure to make deposit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    buttonView.setEnabled(false);
                                }

                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PendingDepositAdapter(List<Calendar> myDataset) {
        mCalendars = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PendingDepositAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_month_deposit_content, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mtvAmount.setText(Utility.formatAmountInRupees(holder.mtvAmount.getContext(), Utility.getMonthlyDepositAmount()));
        holder.mcbxMonth.setText(Utility.readableDepositMonth(mCalendars.get(position)));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCalendars.size();
    }

}
