package com.razikallayi.suraksha.deposit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 13-04-2016.
 */
public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.ViewHolder> {

    private Context mContext;
    private List<Transaction> mDepositedTxnList;
    private Member mMember;
    private int mLateDepositBgColor;
    private DepositFragment.OnMakeDepositListener onMakeDepositListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    DepositAdapter(Context context, Member member) {
        mContext = context;
        mMember = member;
        mDepositedTxnList = member.fetchDeposits(context);
        mLateDepositBgColor= mContext.getResources().getColor(R.color.lateDepositBgColor);
    }

    public void setOnMakeDepositListener(DepositFragment.OnMakeDepositListener listener){
        onMakeDepositListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DepositAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deposited_list_item, parent, false);
        // set the view's size, margins, padding and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mAmountTextView.setText(Utility.formatAmountInRupees(holder.mAmountTextView.getContext(),
//                Utility.getMonthlyDepositAmount()));
        Transaction depositedTxns = mDepositedTxnList.get(position);
        holder.mCountTextView.setText(String.valueOf(this.getItemCount() - position));
        long depositMonth = depositedTxns.getDefinedDepositMonth();
        String monthAndYear = CalendarUtils.readableDepositMonth(depositMonth);
        holder.mMonthTextView.setText(monthAndYear);
        long createdAt = depositedTxns.getCreatedAt();
        holder.mCreatedAtTextView.setText(CalendarUtils.getFriendlyDayString(mContext,createdAt));
        if(depositedTxns.isLateDeposit()){
            holder.mDepositItemLayout.setBackgroundColor(mLateDepositBgColor);
        }else{
            holder.mDepositItemLayout.setBackgroundColor(1);//white
        }
        holder.mOfficerTextView.setText(Officer.getOfficerFromId(mContext, depositedTxns.getOfficer_id()).getName());
        if(!depositedTxns.getNarration().equals("")) {
            holder.mRemarksTextView.setText(depositedTxns.getNarration());
            holder.mRemarksTextView.setVisibility(View.VISIBLE);
        }else{
            holder.mRemarksTextView.setVisibility(View.GONE);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDepositedTxnList.size();
    }

    void onMakeDepositClick(View view, LayoutInflater inflater) {
        //Get Next deposit Month
        Calendar depositMonth = mMember.getNextDepositMonthCalendar(mDepositedTxnList);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Deposit " + (int) Utility.getMonthlyDepositAmount() + " in account " + mMember.getAccountNo() + " ?");
        builder.setTitle(CalendarUtils.readableDepositMonth(depositMonth));

        final View remarks_dialog_content = inflater.inflate(R.layout.remarks_dialog_content, null);
        builder.setView(remarks_dialog_content);
        final Calendar nextDepositMonth = depositMonth;
        builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.txtRemarks)).getText().toString();
                makeDeposit(nextDepositMonth.getTimeInMillis(), remarks);
                if (onMakeDepositListener != null) {
                    onMakeDepositListener.onMakeDeposit();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void makeDeposit(long depositMonth, String remarks) {
        Transaction txn = mMember.makeDeposit(mContext, depositMonth, remarks);
        mDepositedTxnList = mMember.fetchDeposits(mContext);
        notifyDataSetChanged();
        if (SmsUtils.smsEnabledAfterDeposit(mContext)) {
            String mobileNumber = mMember.getMobile();
            String message = mMember.getName() + ", your " + mContext.getResources().getString(R.string.app_name)
                    + " account " + mMember.getAccountNo()
                    + " is credited with a deposit of " + (int) txn.getAmount()
                    + " " + CalendarUtils.readableDepositMonth(txn.getDefinedDepositMonth());
            boolean result = SmsUtils.sendSms(message, mobileNumber);
            if (result) {
                Toast.makeText(mContext, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        RelativeLayout mDepositItemLayout;
//      TextView mAmountTextView;
        TextView mCountTextView;
        TextView mCreatedAtTextView;
        TextView mOfficerTextView;
        TextView mRemarksTextView;
        TextView mMonthTextView;

        public ViewHolder(View v) {
            super(v);
            mDepositItemLayout = (RelativeLayout) v.findViewById(R.id.deposit_item_layout);
            mMonthTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositMonth);
//          mAmountTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositAmount);
            mCountTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositCount);
            mCreatedAtTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositCreatedAt);
            mOfficerTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositOfficer);
            mRemarksTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositRemarks);
        }
    }
}