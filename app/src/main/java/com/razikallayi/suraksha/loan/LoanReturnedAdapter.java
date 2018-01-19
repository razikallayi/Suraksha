package com.razikallayi.suraksha.loan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.List;

/**
 * Created by Razi Kallayi on 04-06-2017 01:27.
 */
public class LoanReturnedAdapter
        extends RecyclerView.Adapter<LoanReturnedAdapter.ViewHolder> {
    private List<Transaction> loanReturnList;
    private Context mContext;


    public LoanReturnedAdapter(List<Transaction> loanReturnList) {
        this.loanReturnList = loanReturnList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loan_return_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mContext = holder.mView.getContext();
        //Todo Change cursor to loan return
//            final LoanIssue loanIssue = LoanIssue.getLoanIssueFromCursor(context, cursor);
        final Transaction loanReturnTxn = loanReturnList.get(position);

        holder.instalment_count_loan_return_item.setText(String.valueOf(loanReturnTxn.getInstalmentNumber()));
        holder.date_loan_return_item.setText(CalendarUtils.formatDate(loanReturnTxn.getPaymentDate()));
        holder.amount_loan_return_item.setText(Utility.formatAmountInRupees(mContext, loanReturnTxn.getAmount()));
        holder.remarks_loan_return_item.setText(loanReturnTxn.getNarration());
        holder.officer_loan_return_item.setText(Officer.getOfficerNameFromId(mContext, loanReturnTxn.getOfficer_id()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, AccountDetailActivity.class);
//                    intent.putExtra("account_number", accountNumber);
//                    context.startActivity(intent);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(mContext, ReturnLoanActivity.class);
                intent.putExtra(ReturnLoanActivity.ARG_LOAN_RETURN_TXN_ID, loanReturnTxn.getId());
                mContext.startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return loanReturnList.size();
    }


//    void onLoanReturnClick(final View view, LayoutInflater inflater, final LoanIssue loanIssue) {
//        Intent intent = new Intent(view.getContext(), ReturnLoanActivity.class);
//        ReturnLoanActivity.setLoanIssue(loanIssue);
//        view.getContext().startActivity(intent);

//        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//        builder.setMessage("Receive " + Utility.formatAmountInRupees(view.getContext(), loanIssue.getLoanInstalmentAmount())
//                + " as loan return "
//                + String.valueOf((int) loanIssue.getLoanReturnTxnList(view.getContext()).size() + 1)
//                + " from " + loanIssue.getMember(view.getContext()).getName());
//        builder.setTitle("Loan Return");
//
//        final View remarks_dialog_content = inflater.inflate(R.layout.remarks_dialog_content, null);
//        builder.setView(remarks_dialog_content);
//        builder.setPositiveButton("Receive Amount", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                    String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.txtRemarks)).getText().toString();
//                    Transaction loanReturn  = loanIssue.saveLoanReturn(mContext,remarks);
//                    if(loanReturn != null){
//                        loanReturnList.add(0,loanReturn);
//                        notifyItemInserted(0);
//                        onLoanReturnListener.onLoanReturn();
//                        if (SmsUtils.smsEnabledAfterLoanReturn(mContext)) {
//                            String mobileNumber = mMember.getMobile();
//                            String message = mMember.getName()+", "+Utility.formatAmountInRupees(mContext, loanReturn.getAmount())
//                                    +" is payed in " + mContext.getResources().getString(R.string.app_name)
//                                    + " account " + mMember.getAccountNo()
//                                    +" as Loan Return "+ loanIssue.getLoanReturnTxnList(getContext()).size()
//                                    ;
//                            boolean result = SmsUtils.sendSms(message, mobileNumber);
//                            if (result) {
//                                Toast.makeText(mContext, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }

//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();

//    }

//    public void onLoanReturn(LoanIssue loanIssue, Transaction loanReturn) {
//        loanReturnList.add(0, loanReturn);
//        notifyItemInserted(0);
//        onLoanReturnClickListener.onLoanReturn(loanIssue, loanReturn);
//        if (SmsUtils.smsEnabledAfterLoanReturn(mContext)) {
//            Member member = loanIssue.getMember(mContext);
//            String mobileNumber = member.getMobile();
//            String message = member.getName() + ", " + Utility.formatAmountInRupees(mContext, loanReturn.getAmount())
//                    + " is payed in " + mContext.getResources().getString(R.string.app_name)
//                    + " account " + member.getAccountNo()
//                    + " as Loan Return " + loanIssue.getLoanReturnTxnList(mContext).size();
//            boolean result = SmsUtils.sendSms(message, mobileNumber);
//            if (result) {
//                Toast.makeText(mContext, "SMS sent to " + member.getName(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView instalment_count_loan_return_item;
        final TextView date_loan_return_item;
        final TextView amount_loan_return_item;
        final TextView remarks_loan_return_item;
        final TextView officer_loan_return_item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            instalment_count_loan_return_item = view.findViewById(R.id.instalment_count_loan_return_item);
            date_loan_return_item = view.findViewById(R.id.date_loan_return_item);
            amount_loan_return_item = view.findViewById(R.id.amount_loan_return_item);
            remarks_loan_return_item = view.findViewById(R.id.remarks_loan_return_item);
            officer_loan_return_item = view.findViewById(R.id.officer_loan_return_item);
        }
    }
}
