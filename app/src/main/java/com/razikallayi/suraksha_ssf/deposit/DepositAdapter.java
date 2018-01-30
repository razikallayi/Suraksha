package com.razikallayi.suraksha_ssf.deposit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.member.Member;
import com.razikallayi.suraksha_ssf.officer.Officer;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.CalendarUtils;

import java.util.List;

/**
 * Created by Razi Kallayi on 13-04-2016.
 */
public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.ViewHolder> {

    private final int mdefaultTextColor;
    private Context mContext;
    private List<Transaction> mDepositedTxnList;
    private Member mMember;
    private int mLateDepositTextColor;

    // Provide a suitable constructor (depends on the kind of dataset)
    DepositAdapter(Context context, Member member) {
        mContext = context;
        mMember = member;
        mDepositedTxnList = member.fetchDeposits(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLateDepositTextColor = mContext.getResources().getColor(R.color.danger, null);
            mdefaultTextColor = mContext.getResources().getColor(R.color.black, null);
        } else {
            mLateDepositTextColor = mContext.getResources().getColor(R.color.danger);
            mdefaultTextColor = mContext.getResources().getColor(R.color.black);
        }
    }

    public void listUpdated() {
        mDepositedTxnList = mMember.fetchDeposits(mContext);
        notifyDataSetChanged();
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
        final Transaction depositedTxn = mDepositedTxnList.get(position);
        holder.mCountTextView.setText(String.valueOf(this.getItemCount() - position));
        long depositMonth = depositedTxn.getDefinedDepositMonth();
        String monthAndYear = CalendarUtils.readableDepositMonth(depositMonth);
        holder.mMonthTextView.setText(monthAndYear);
        long paymentDate = depositedTxn.getPaymentDate();
        holder.mPaymentDateTextView.setText(CalendarUtils.getFriendlyDayString(mContext, paymentDate));
        if (depositedTxn.isLateDeposit()) {
            holder.mMonthTextView.setTextColor(mLateDepositTextColor);
            holder.mPaymentDateTextView.setPaintFlags(holder.mPaymentDateTextView.getPaintFlags()
                    | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            holder.mMonthTextView.setTextColor(mdefaultTextColor);
            holder.mPaymentDateTextView.setPaintFlags(0);
        }
        holder.mOfficerTextView.setText(Officer.getOfficerFromId(mContext, depositedTxn.getOfficer_id()).getName());
        if (!depositedTxn.getNarration().equals("")) {
            holder.mRemarksTextView.setText(depositedTxn.getNarration());
            holder.mRemarksTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mRemarksTextView.setVisibility(View.GONE);
        }
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(view.getContext(), MakeDepositActivity.class);
                intent.putExtra(MakeDepositActivity.ARG_MEMBER_ID, mMember.getId());
                intent.putExtra(MakeDepositActivity.ARG_DEPOSIT_TXN_ID, depositedTxn.getId());
                view.getContext().startActivity(intent);
                return true;
            }
        });
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
        RelativeLayout mDepositItemLayout;
        //      TextView mAmountTextView;
        TextView mCountTextView;
        TextView mPaymentDateTextView;
        TextView mOfficerTextView;
        TextView mRemarksTextView;
        TextView mMonthTextView;
        View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mDepositItemLayout = v.findViewById(R.id.deposit_item_layout);
            mMonthTextView = mDepositItemLayout.findViewById(R.id.depositMonth);
//          mAmountTextView = (TextView) mDepositItemLayout.findViewById(R.id.depositAmount);
            mCountTextView = mDepositItemLayout.findViewById(R.id.depositCount);
            mPaymentDateTextView = mDepositItemLayout.findViewById(R.id.depositPaymentDate);
            mOfficerTextView = mDepositItemLayout.findViewById(R.id.depositOfficer);
            mRemarksTextView = mDepositItemLayout.findViewById(R.id.depositRemarks);
        }
    }
}
