package com.razikallayi.suraksha.account;

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
public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.ViewHolder>
        implements DepositFragment.OnMakeDepositClickListener {

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


    private Context mContext;
    private List<Transaction> mDepositedTxnList;
    private Account mAccount;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DepositAdapter(Context context, Account account) {
        mAccount = account;
        mContext = context;
        mDepositedTxnList = account.fetchDeposits(context);
        ;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DepositAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deposited_list_item, parent, false);
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
        holder.mCreatedAtTextView.setText(CalendarUtils.formatDateTime(deposited.getCreatedAt()));
        holder.mCreatedAtTextView.setVisibility(View.VISIBLE);
        holder.mOfficerTextView.setText(Officer.getOfficerFromId(mContext,deposited.getOfficer_id()).getName());
        holder.mOfficerTextView.setVisibility(View.VISIBLE);
        holder.mRemarksTextView.setText(deposited.getNarration());
        holder.mRemarksTextView.setVisibility(View.VISIBLE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDepositedTxnList.isEmpty()) {
            return 0;
        } else {
            return mDepositedTxnList.size();
        }
    }

    @Override
    public void onMakeDepositClick(View view, LayoutInflater inflater) {
        Calendar depositMonth = CalendarUtils.getInstance();
        if (mDepositedTxnList.isEmpty()) {
            depositMonth = CalendarUtils.getSurakshaStartDate();
        } else {
            Transaction month = mDepositedTxnList.get(0);
            depositMonth.setTimeInMillis(month.getDefinedDepositMonth());
            depositMonth.add(Calendar.MONTH, 1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Deposit " + Utility.formatAmountInRupees(mContext,
                Utility.getMonthlyDepositAmount()) + " in account " + mAccount.getAccountNumber() + " ?");
        builder.setTitle(CalendarUtils.readableDepositMonth(depositMonth));

        final View remarks_dialog_content = inflater.inflate(R.layout.remarks_dialog_content, null);
        builder.setView(remarks_dialog_content);
        final Calendar nextDepositMonth = depositMonth;
        builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.remarksEditText)).getText().toString();
                makeDeposit(nextDepositMonth.getTimeInMillis(), remarks);
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

    public void makeDeposit(long depositMonth, String remarks) {
        Transaction txn = mAccount.makeDeposit(mContext, depositMonth, remarks);
        mDepositedTxnList = mAccount.fetchDeposits(mContext);
        notifyDataSetChanged();
        if (SmsUtils.smsEnabledAfterDeposit(mContext)) {
            Member member = mAccount.getMember();
            String phoneNumber = member.getMobile();
            String message = member.getName()+", your suraksha account "+mAccount.getAccountNumber()
                    +" is credited with a deposit of "+Utility.formatAmountInRupees(mContext,txn.getAmount())
                    +" for the month "+CalendarUtils.readableDepositMonth(txn.getDefinedDepositMonth());
            boolean result = SmsUtils.sendSms(message,phoneNumber);
            if(result) {
                Toast.makeText(mContext, "SMS sent to " + member.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
