package com.razikallayi.suraksha.loan;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.Utility;

/**
 * Created by Razi Kallayi on 01-07-2016 01:24.
 */
public class LoanIssuedAdapter
        extends RecyclerViewCursorAdapter<LoanIssuedAdapter.ViewHolder> {

    private static final int ACTIVE_LOAN = 1;
    private static final int INACTIVE_LOAN = 2;
    private int disabledColor;

    public LoanIssuedAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        if (viewType == ACTIVE_LOAN) {
            layout = R.layout.loan_issued_list_item_active;
        } else {
            layout = R.layout.loan_issued_list_item;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        disabledColor = view.getContext().getResources().getColor(R.color.yellow_dull);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor, int position) {
        // Set selected state; use a state list drawable to style the view
        holder.bindData(cursor);
    }

    @Override
    public int getItemViewType(int position) {
        final Cursor cursor = this.getItem(position);
        long closedAt = 0;
        if (cursor != null && cursor.getCount() > 0) {
//            if (cursor.getCount() == 1) {
//                cursor.moveToFirst();
//            }
            closedAt = cursor.getLong(LoanIssue.LoanIssueQuery.COL_CLOSED_AT);
        }
        if (closedAt == 0) {
            return ACTIVE_LOAN;
        } else {
            return INACTIVE_LOAN;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final View LoanIssuedItemView;
        final TextView amountLoanIssueItem;
        final TextView guarantorNameLoanIssueItem;
        final TextView issued_date_loan_issue_item;
        final TextView purpose_loan_issue_item;
        final TextView office_statement_loan_issue_item;
        final TextView officer_loan_issue_item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            LoanIssuedItemView = view.findViewById(R.id.cardVIewLoanIssuedItem);
            amountLoanIssueItem = view.findViewById(R.id.amountLoanIssueItem);
            guarantorNameLoanIssueItem = view.findViewById(R.id.guarantorNameLoanIssueItem);
            issued_date_loan_issue_item = view.findViewById(R.id.issued_date_loan_issue_item);
            purpose_loan_issue_item = view.findViewById(R.id.purpose_loan_issue_item);
            office_statement_loan_issue_item = view.findViewById(R.id.office_statement_loan_issue_item);
            officer_loan_issue_item = view.findViewById(R.id.officer_loan_issue_item);
        }


        void bindData(final Cursor cursor) {
            final Context context = mView.getContext();
            final LoanIssue loanIssue = LoanIssue.getLoanIssueFromCursor(context, cursor);
            if (loanIssue.isClosed()) {
                mView.setBackgroundColor(disabledColor);
            } else {
                mView.setBackgroundColor(0);
            }
            amountLoanIssueItem.setText(Utility.formatAmountInRupees(context, loanIssue.getAmount()));
            guarantorNameLoanIssueItem.setText(Member.getMemberFromAccountNumber(
                    context, loanIssue.getSecurityAccountNo()).getName() + " [" + loanIssue.getSecurityAccountNo() + "]");
            issued_date_loan_issue_item.setText(CalendarUtils.formatDate(loanIssue.getIssuedAt()));
            purpose_loan_issue_item.setText(loanIssue.getPurpose());
            office_statement_loan_issue_item.setText(loanIssue.getOfficeStatement());
            if (loanIssue.getPurpose() == null) {
                purpose_loan_issue_item.setVisibility(View.GONE);
            } else {
                purpose_loan_issue_item.setVisibility(View.VISIBLE);
            }
            if (loanIssue.getOfficeStatement() == null) {
                office_statement_loan_issue_item.setVisibility(View.GONE);
            } else {
                office_statement_loan_issue_item.setVisibility(View.VISIBLE);
            }
            officer_loan_issue_item.setText(Officer.getOfficerNameFromId(
                    context, loanIssue.getTransaction().getOfficer_id()));
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), LoanReturnedListActivity.class);
                    intent.putExtra(LoanReturnedListActivity.ARG_LOAN_ISSUE_ID, loanIssue.getId());
                    view.getContext().startActivity(intent);
                }
            });
            mView.setLongClickable(true);
            if (AuthUtils.isAdmin(mView.getContext())) {
                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, IssueLoanActivity.class);
                        intent.putExtra(IssueLoanActivity.ARG_MEMBER_ID, loanIssue.getMember(context).getId());
                        intent.putExtra(IssueLoanActivity.ARG_LOAN_ISSUE_ID, loanIssue.getId());
                        context.startActivity(intent);
                        return true;
                    }
                });
            }
        }
    }
}
