package com.razikallayi.suraksha.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.List;

/**
 * Created by Razi Kallayi on 13-04-2016.
 */
public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.ViewHolder> {

    private Context mContext;
    private List<Transaction> mDepositedTxnList;
    private Account mAccount;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DepositAdapter(Context context, Account account) {
        mAccount = account;
        mContext = context;
        mDepositedTxnList = account.fetchDeposits(context);;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DepositAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_month_deposit_content, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mAmountTextView.setText(Utility.formatAmountInRupees(holder.mAmountTextView.getContext(),
                Utility.getMonthlyDepositAmount()));
        Transaction deposited = mDepositedTxnList.get(position);
        String monthAndYear = CalendarUtils.readableDepositMonth(deposited.getDefinedDepositMonth());
        holder.mMonthCheckbox.setText(monthAndYear);
        holder.mCreatedAtTextView.setText(CalendarUtils.formatDate(deposited.getCreatedAt()));
        holder.mCreatedAtTextView.setVisibility(View.VISIBLE);
        holder.mOfficerTextView.setText(deposited.getOfficer().getName());
        holder.mOfficerTextView.setVisibility(View.VISIBLE);
        holder.mRemarksTextView.setText(deposited.getNarration());
        holder.mRemarksTextView.setVisibility(View.VISIBLE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDepositedTxnList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout mDepositItemLayout;
        public TextView mAmountTextView;
        public TextView mCreatedAtTextView;
        public TextView mOfficerTextView;
        public TextView mRemarksTextView;
        public TextView mMonthCheckbox;

        public ViewHolder(View v) {
            super(v);
            mDepositItemLayout = (RelativeLayout) v.findViewById(R.id.deposit_item_layout);
            mMonthCheckbox = (TextView) mDepositItemLayout.findViewById(R.id.depositMonth);
            mAmountTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositAmount);
            mCreatedAtTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositCreatedAt);
            mOfficerTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositOfficer);
            mRemarksTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositRemarks);
        }
    }

    public void makeDeposit(long depositMonth, String remarks){
        mAccount.makeDeposit(mContext, depositMonth, remarks);
        notifyDataSetChanged();
    }


}
