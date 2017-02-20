package com.razikallayi.suraksha.loan;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.List;

public class LoanReturnedFragment extends Fragment {
    public static final String TAG = LoanReturnedFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_LOAN_ISSUE_ID = "loan_issue_id";
    private static final int LOAN_RETURN_LOADER = 0X100;
    private long loanIssueId;
    private View rootView;
    private Context mContext;
    private Member mMember;

    public LoanReturnedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.loan_return_fragment, container, false);
        mContext = this.getContext();

        loanIssueId = getArguments().getLong(ARG_LOAN_ISSUE_ID, -1);
        final LoanIssue loanIssue = LoanIssue.getLoanIssue(mContext, loanIssueId);

        RecyclerView loanReturnedRecyclerView = (RecyclerView) rootView.findViewById(R.id.loan_return_list);
        final LoanReturnedAdapter loanReturnedAdapter = new LoanReturnedAdapter(loanIssue.getLoanReturnTxnList(mContext));
        if (null != loanReturnedRecyclerView) {
            // specify an adapter
            loanReturnedRecyclerView.setAdapter(loanReturnedAdapter);
            loanReturnedRecyclerView.setHasFixedSize(true);
            loanReturnedRecyclerView.setNestedScrollingEnabled(false);

            //        // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            loanReturnedRecyclerView.setLayoutManager(layoutManager);

        }

        int accountNumber = loanIssue.getAccountNumber();
        mMember = Member.getMemberFromAccountNumber(mContext, accountNumber);

        TextView memberNameTv = (TextView) rootView.findViewById(R.id.tvMemberName);
        memberNameTv.setText(mMember.getName());
        TextView memberAddressTv = (TextView) rootView.findViewById(R.id.tvMemberAddress);
        memberAddressTv.setText(mMember.getAddress());
        TextView memberAcNoTv = (TextView) rootView.findViewById(R.id.tvAcNo);
        memberAcNoTv.setText(String.valueOf(mMember.getAccountNo()));

        final Button loanReturnButton = (Button) rootView.findViewById(R.id.tvActonButton);
        removeButtonIfLoanClosed(loanReturnButton,loanIssue);
        loanReturnButton.setText("Loan Return");
        loanReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loanReturnedAdapter.onLoanReturnClick(view, inflater, loanIssue);
            }
        });

        loanReturnedAdapter.setOnLoanReturnListener(new OnLoanReturnListener() {
            @Override
            public void onLoanReturn() {
                removeButtonIfLoanClosed(loanReturnButton,loanIssue);
            }
        });


        return rootView;
    }

    public void removeButtonIfLoanClosed(View loanReturnButton, LoanIssue loanIssue){
        if(loanIssue.isClosed()){
            loanReturnButton.setVisibility(View.GONE);
        }else {
            loanReturnButton.setVisibility(View.VISIBLE);
        }
    }

    // The callback interface
    public interface OnLoanReturnListener {
        void onLoanReturn();
    }

    public class LoanReturnedAdapter
            extends RecyclerView.Adapter<ViewHolder> {
        List<Transaction> loanReturnList;
        private LoanReturnedFragment.OnLoanReturnListener onLoanReturnListener;

        public LoanReturnedAdapter(List<Transaction> loanReturnList) {
            this.loanReturnList = loanReturnList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loan_return_list_item, parent, false);
            return new ViewHolder(view);
        }

        public void setOnLoanReturnListener(LoanReturnedFragment.OnLoanReturnListener listener) {
            onLoanReturnListener = listener;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Context context = holder.mView.getContext();
            //Todo Change cursor to loan return
//            final LoanIssue loanIssue = LoanIssue.getLoanIssueFromCursor(context, cursor);
            final Transaction loanReturnTxn = loanReturnList.get(position);

            holder.instalment_count_loan_return_item.setText(String.valueOf(loanReturnList.size() - position));
            holder.date_loan_return_item.setText(CalendarUtils.formatDate(loanReturnTxn.getCreatedAt()));
            holder.amount_loan_return_item.setText(Utility.formatAmountInRupees(context, loanReturnTxn.getAmount()));
            holder.remarks_loan_return_item.setText(loanReturnTxn.getNarration());
            holder.officer_loan_return_item.setText(Officer.getOfficerNameFromId(context, loanReturnTxn.getOfficer_id()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
//                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, AccountDetailActivity.class);
//                    intent.putExtra("account_number", accountNumber);
//                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return loanReturnList.size();
        }


        void onLoanReturnClick(final View view, LayoutInflater inflater, final LoanIssue loanIssue) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Receive "+Utility.formatAmountInRupees(mContext,loanIssue.getLoanInstalmentAmount())
                    +" as loan return "
                    +String.valueOf((int)loanIssue.getLoanReturnTxnList(getContext()).size()+1)
                    +" from " + mMember.getName());
            builder.setTitle("Loan Return");

            final View remarks_dialog_content = inflater.inflate(R.layout.remarks_dialog_content, null);
            builder.setView(remarks_dialog_content);
            builder.setPositiveButton("Receive Amount", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.txtRemarks)).getText().toString();
                    Transaction loanReturn  = loanIssue.saveLoanReturn(mContext,remarks);
                    if(loanReturn != null){
                        loanReturnList.add(loanReturn);
                        notifyDataSetChanged();
                        onLoanReturnListener.onLoanReturn();
                        if (SmsUtils.smsEnabledAfterLoanReturn(mContext)) {
                            String mobileNumber = mMember.getMobile();
                            String message = mMember.getName()+", "+Utility.formatAmountInRupees(mContext, loanReturn.getAmount())
                                    +" is payed in " + mContext.getResources().getString(R.string.app_name)
                                    + " account " + mMember.getAccountNo()
                                    +" as Loan Return "+ loanIssue.getLoanReturnTxnList(getContext()).size()
                                    ;
                            boolean result = SmsUtils.sendSms(message, mobileNumber);
                            if (result) {
                                Toast.makeText(mContext, "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
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
    }

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
            instalment_count_loan_return_item = (TextView) view.findViewById(R.id.instalment_count_loan_return_item);
            date_loan_return_item = (TextView) view.findViewById(R.id.date_loan_return_item);
            amount_loan_return_item = (TextView) view.findViewById(R.id.amount_loan_return_item);
            remarks_loan_return_item = (TextView) view.findViewById(R.id.remarks_loan_return_item);
            officer_loan_return_item = (TextView) view.findViewById(R.id.officer_loan_return_item);
        }
    }
}