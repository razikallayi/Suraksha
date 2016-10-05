package com.razikallayi.suraksha.loan;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

/**
 * Created by Razi Kallayi on 01-07-2016 01:24.
 */
public class LoanReturnedRecyclerViewAdapter
        extends RecyclerViewCursorAdapter<LoanReturnedRecyclerViewAdapter.ViewHolder> {

    public LoanReturnedRecyclerViewAdapter() {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView amountLoanIssueItem;
        public final TextView guarantorNameLoanIssueItem;
        public final TextView issued_date_loan_issue_item;
        public final TextView purpose_loan_issue_item;
        public final TextView office_statement_loan_issue_item;
        public final TextView officer_loan_issue_item;

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

        public void bindData(final Cursor cursor) {

            final Context context = mView.getContext();
            final LoanIssue loanIssue = LoanIssue.getLoanIssueFromCursor(context, cursor);

            amountLoanIssueItem.setText(Utility.formatAmountInRupees(context, loanIssue.getAmount()));
            guarantorNameLoanIssueItem.setText(Member.getMemberFromAccountNumber(
                    context, loanIssue.getSecurityAccountNo()).getName());
            issued_date_loan_issue_item.setText(CalendarUtils.formatDate(loanIssue.getCreatedAt()));
            purpose_loan_issue_item.setText(loanIssue.getPurpose());
            office_statement_loan_issue_item.setText(loanIssue.getOfficeStatement());
            officer_loan_issue_item.setText(Officer.getOfficerNameFromId(
                    context, loanIssue.getTransaction().getOfficer_id()));
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, AccountDetailActivity.class);
//                    intent.putExtra("account_number", accountNumber);
//                    context.startActivity(intent);
                }
            });
        }
    }
}
