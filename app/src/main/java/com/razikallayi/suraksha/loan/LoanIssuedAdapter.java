package com.razikallayi.suraksha.loan;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

/**
 * Created by Razi Kallayi on 01-07-2016 01:24.
 */
public class LoanIssuedAdapter
        extends RecyclerViewCursorAdapter<LoanIssuedAdapter.ViewHolder> {

    private FragmentManager mFragmentManager;

    public LoanIssuedAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loan_issued_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        // Set selected state; use a state list drawable to style the view
        holder.bindData(cursor);
    }


    void onItemClick(View view, long loanIssueId) {
        LoanReturnedFragment loanReturnedFragment = new LoanReturnedFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(LoanReturnedFragment.ARG_LOAN_ISSUE_ID, loanIssueId);
        loanReturnedFragment.setArguments(bundle);
        if (mFragmentManager != null) {
            mFragmentManager.beginTransaction()
                    .addToBackStack(loanReturnedFragment.getTag())
                    .replace(R.id.loanFragment, loanReturnedFragment)
                    .commit();
        }
    }

    public void setFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView amountLoanIssueItem;
        final TextView guarantorNameLoanIssueItem;
        final TextView issued_date_loan_issue_item;
        final TextView purpose_loan_issue_item;
        final TextView office_statement_loan_issue_item;
        final TextView officer_loan_issue_item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            amountLoanIssueItem = (TextView) view.findViewById(R.id.amountLoanIssueItem);
            guarantorNameLoanIssueItem = (TextView) view.findViewById(R.id.guarantorNameLoanIssueItem);
            issued_date_loan_issue_item = (TextView) view.findViewById(R.id.issued_date_loan_issue_item);
            purpose_loan_issue_item = (TextView) view.findViewById(R.id.purpose_loan_issue_item);
            office_statement_loan_issue_item = (TextView) view.findViewById(R.id.office_statement_loan_issue_item);
            officer_loan_issue_item = (TextView) view.findViewById(R.id.officer_loan_issue_item);
        }

        void bindData(final Cursor cursor) {

            final Context context = mView.getContext();
            final LoanIssue loanIssue = LoanIssue.getLoanIssueFromCursor(context, cursor);
            amountLoanIssueItem.setText(Utility.formatAmountInRupees(context, loanIssue.getAmount()));
            guarantorNameLoanIssueItem.setText(Member.getMemberFromAccountNumber(
                    context, loanIssue.getSecurityAccountNo()).getName() + " ["+loanIssue.getSecurityAccountNo()+"]");
            issued_date_loan_issue_item.setText(CalendarUtils.formatDate(loanIssue.getCreatedAt()));
            purpose_loan_issue_item.setText(loanIssue.getPurpose());
            office_statement_loan_issue_item.setText(loanIssue.getOfficeStatement());
            officer_loan_issue_item.setText(Officer.getOfficerNameFromId(
                    context, loanIssue.getTransaction().getOfficer_id()));
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    onItemClick(mView, loanIssue.getId());
//                    Intent intent = new Intent(context, AccountDetailActivity.class);
//                    intent.putExtra("account_number", accountNumber);
//                    context.startActivity(intent);
                }
            });
        }
    }
}
