package com.razikallayi.suraksha.report;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.Utility;
import com.razikallayi.suraksha.utils.WordUtils;

import java.util.List;

public class PeriodicalTransactionAdapter extends RecyclerView.Adapter<PeriodicalTransactionAdapter.DailyTxnViewHolder> {
    List<DailyItem> mDailyItems;

    public PeriodicalTransactionAdapter(List<DailyItem> dailyItems) {
        super();
        this.mDailyItems = dailyItems;
    }

    @Override
    public DailyTxnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.periodic_txn_item, parent, false);
        return new DailyTxnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyTxnViewHolder holder, int position) {
        Context context = holder.mView.getContext();
        holder.label.setText(WordUtils.toTitleCase(Transaction.getLedgerName(mDailyItems.get(position).label)));
        holder.count.setText(String.valueOf(mDailyItems.get(position).count));
        holder.amount.setText(Utility.formatAmountInRupees(context, mDailyItems.get(position).amount));
    }

    @Override
    public int getItemCount() {
        if (mDailyItems == null) return 0;
        return mDailyItems.size();
    }


    public class DailyTxnViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView label;
        public final TextView count;
        public TextView amount;

        public DailyTxnViewHolder(View view) {
            super(view);
            mView = view;
            label = view.findViewById(R.id.label);
            count = view.findViewById(R.id.count);
            amount = view.findViewById(R.id.amount);
        }
    }
}
