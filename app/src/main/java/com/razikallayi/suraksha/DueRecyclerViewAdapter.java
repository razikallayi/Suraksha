package com.razikallayi.suraksha;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.Calendar;
import java.util.List;


public class DueRecyclerViewAdapter extends RecyclerView.Adapter<DueRecyclerViewAdapter.ViewHolder> {

    private final List<Calendar> mDues;
    private final int  mAccountNumber;

    public DueRecyclerViewAdapter(int accountNumber, List<Calendar> items) {
        mDues = items;
        mAccountNumber =accountNumber;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.due_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mDue = mDues.get(position);

        //Ledger
        holder.mLedger.setText(Transaction.getLedgerName(SurakshaContract.TxnEntry.DEPOSIT_LEDGER));
        //month
        holder.mMonthView.setText(CalendarUtils.readableDepositMonth(mDues.get(position).getTimeInMillis()));

        //Amount
        holder.mAmountView.setText(holder.mAmountView.getContext().getString(R.string.format_rupees, Utility.getMonthlyDepositAmount()));

        holder.mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Deposit "+Utility.formatAmountInRupees(v.getContext(),Utility.getMonthlyDepositAmount())
                        + " in Ac/No: "+String.valueOf(mAccountNumber)
                        +" for the month of "+ CalendarUtils.readableDepositMonth(holder.mDue.getTimeInMillis())
                        )
                        .setTitle("Make Deposit?")
                        .setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PayDueTask payDueTask = new PayDueTask(builder.getContext(), holder);
                                payDueTask.execute(holder.mDue.getTimeInMillis());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLedger;
        public final TextView mMonthView;
        public final TextView mAmountView;
        public final TextView mPayButton;
        public Calendar mDue;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLedger = (TextView) view.findViewById(R.id.txnLedger);
            mMonthView = (TextView) view.findViewById(R.id.txnMonth);
            mAmountView = (TextView) view.findViewById(R.id.txnAmount);
            mPayButton = (TextView) view.findViewById(R.id.payButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMonthView.getText() + "'";
        }
    }
    private class PayDueTask extends AsyncTask<Long, Void, Boolean> {
        private Context mContext;
        private RecyclerView.ViewHolder mHolder;


        public PayDueTask(Context mContext, RecyclerView.ViewHolder holder) {
            this.mContext = mContext;
            this.mHolder = holder;
        }

        @Override
        protected Boolean doInBackground(Long... months) {
            //Save Registration Fee
            Transaction txnPayDue = new Transaction(mContext,mAccountNumber,
                    Utility.getMonthlyDepositAmount(),
                    SurakshaContract.TxnEntry.RECEIPT_VOUCHER,
                    SurakshaContract.TxnEntry.DEPOSIT_LEDGER,
                    "Monthly Deposit", AuthUtils.getAuthenticatedOfficerId(mContext));
            txnPayDue.setDepositForDate(months[0]);
            ContentValues values = Transaction.getTxnContentValues(txnPayDue);
            Cursor cursorDataExist = mContext.getContentResolver()
                    .query(SurakshaContract.TxnEntry.CONTENT_URI,
                            new String[]{SurakshaContract.TxnEntry._ID},
                            SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER +" = ? AND "
                                    + SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE +" = ? ",
                            new String[]{String.valueOf(mAccountNumber),String.valueOf(months[0])},null);

            Uri success=null;
            if (cursorDataExist.getCount()<=0) {
                success= mContext.getContentResolver()
                        .insert(SurakshaContract.TxnEntry.CONTENT_URI, values);

            }
            cursorDataExist.close();
            return success != null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(mContext, "Deposit Successful", Toast.LENGTH_SHORT).show();
                mDues.remove(mHolder.getAdapterPosition());
                notifyItemRemoved(mHolder.getAdapterPosition());
            }
            else {
                Toast.makeText(mContext, "Deposit Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
