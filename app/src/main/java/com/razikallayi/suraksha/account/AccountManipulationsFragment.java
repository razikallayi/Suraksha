package com.razikallayi.suraksha.account;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.loan.IssueLoanActivity;

public class AccountManipulationsFragment extends Fragment {
    public static final String TAG = AccountManipulationsFragment.class.getSimpleName();

//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final View mView;
//        public final TextView mAccountNumber;
//
//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            mAccountNumber = (TextView) view.findViewById(R.id.tvAccountNumber);
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + " '" + mAccountNumber.getText() + "'";
//        }
//    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MEMBER_ID = "member_id";
    public static final String ARG_ACCOUNT_NUMBER = "account_number";

    private static final int MAKE_DEPOSIT_ACTIVITY = 1;
    private static final int ISSUE_LOAN_ACTIVITY = 2;
    private final int ACCOUNT_LIST_LOADER = 1;
    private long mMemberId;
    private long mAccountNumber;
//    private AccountRecyclerViewAdapter mAccountListAdapter;

    public AccountManipulationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_manipulations_fragment, container, false);
        LinearLayout llDepositButton = (LinearLayout) rootView.findViewById(R.id.llDeposit);
        llDepositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MakeDepositActivity.class);
                intent.putExtra(MakeDepositActivity.ARG_ACCOUNT_NUMBER, mAccountNumber);
                startActivityForResult(intent, MAKE_DEPOSIT_ACTIVITY);
            }
        });
        LinearLayout llIssueLoan = (LinearLayout) rootView.findViewById(R.id.llIssueLoan);
        llIssueLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), IssueLoanActivity.class);
                intent.putExtra(IssueLoanActivity.ARG_ACCOUNT_NUMBER, mAccountNumber);
                intent.putExtra(IssueLoanActivity.ARG_MEMBER_ID, mMemberId);
                startActivityForResult(intent, ISSUE_LOAN_ACTIVITY);
            }
        });
//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.account_list);
//        mAccountListAdapter = new AccountRecyclerViewAdapter();
//        recyclerView.setAdapter(mAccountListAdapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMemberId = getArguments().getLong(ARG_MEMBER_ID);
        mAccountNumber = getArguments().getLong(ARG_ACCOUNT_NUMBER);
//            getLoaderManager().initLoader(ACCOUNT_LIST_LOADER, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.account_list_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.add_account) {
//            Intent intent = new Intent(getContext(), CreateAccountActivity.class);
//            intent.putExtra(ARG_MEMBER_ID, mMemberId);
//            startActivityForResult(intent, CREATE_ACCOUNT_ACTIVITY);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(getContext(), SurakshaContract.AccountEntry.buildAccountsOfMemberUri(mMemberId),
//                Account.AccountQuery.PROJECTION, null, null, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mAccountListAdapter.swapCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }


//    public class AccountRecyclerViewAdapter
//            extends RecyclerViewCursorAdapter<AccountManipulationsFragment.ViewHolder> {
//
//        public AccountRecyclerViewAdapter() {
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(getActivity())
//                    .inflate(R.layout.account_list_content, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
//            final int accountNumber = cursor.getInt(Account.AccountQuery.COL_ACCOUNT_NUMBER);
//            holder.mAccountNumber.setText(String.valueOf(accountNumber));
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, AccountDetailActivity.class);
//                    intent.putExtra("account_number", accountNumber);
//                    context.startActivity(intent);
//                }
//            });
//        }
//    }
}
