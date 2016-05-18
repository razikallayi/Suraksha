package com.razikallayi.suraksha.txn;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.utils.Utility;

import java.util.List;


public class TxnRecyclerViewAdapter extends RecyclerView.Adapter<TxnRecyclerViewAdapter.ViewHolder> {

    private final List<Transaction> mTxns;
    //private final OnItemClickListener mListener;

    public TxnRecyclerViewAdapter(List<Transaction> items) {
        mTxns = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.txn_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTxn = mTxns.get(position);

        //Ledger
        holder.mLedger.setText(Transaction.getLedgerName(mTxns.get(position).getLedger()));
        //month
        long month = mTxns.get(position).getDefinedDepositMonth();
        if(month>0) {
            holder.mMonthView.setText(Utility.readableDepositMonth(month));
        }else{
            holder.mMonthView.setVisibility(View.GONE);
        }
        //CreatedAt

        holder.mCreatedAt.setText(Utility.formatDate(Long.valueOf(mTxns.get(position).getCreatedAt())));
        //Amount

        holder.mAmountView.setText(holder.mAmountView.getContext().getString(R.string.format_rupees,mTxns.get(position).getAmount()));
        holder.mVoucherName.setText(Transaction.getVoucherName(mTxns.get(position).getVoucherType()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onItemClick(holder.mTxn);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTxns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLedger;
        public final TextView mMonthView;
        public final TextView mCreatedAt;
        public final TextView mAmountView;
        public final TextView mVoucherName;
        public Transaction mTxn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLedger = (TextView) view.findViewById(R.id.ledger);
            mMonthView = (TextView) view.findViewById(R.id.month);
            mCreatedAt = (TextView) view.findViewById(R.id.createdAt);
            mAmountView = (TextView) view.findViewById(R.id.amount);
            mVoucherName = (TextView) view.findViewById(R.id.voucherName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMonthView.getText() + "'";
        }
    }
}
