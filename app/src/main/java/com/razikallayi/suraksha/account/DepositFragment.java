package com.razikallayi.suraksha.account;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.razikallayi.suraksha.R;

public class DepositFragment extends Fragment {
    public static final String TAG = DepositFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private int mAccountNumber;
    private Account mAccount;
    private Context mContext;
    private RecyclerView mDepositRecyclerView;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getContext();

        final View rootView = inflater.inflate(R.layout.deposit_fragment, container, false);
        mDepositRecyclerView = (RecyclerView) rootView.findViewById(R.id.deposited_list);
        mDepositRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mDepositRecyclerView.setLayoutManager(layoutManager);

        //get AccountNumber From Intent
        mAccountNumber = getArguments().getInt(ARG_ACCOUNT_NUMBER);
        mAccount = Account.getAccountFromAccountNumber(mContext, mAccountNumber);

        TextView memberNameTv = (TextView) rootView.findViewById(R.id.depositMemberName);
        memberNameTv.setText(mAccount.getMember().getName());
        TextView memberAcNoTv = (TextView) rootView.findViewById(R.id.depositMemberAcNo);
        memberAcNoTv.setText(String.valueOf(mAccount.getAccountNumber()));

        // specify an adapter
        final DepositAdapter depositAdapter = new DepositAdapter(getContext(), mAccount);
        mDepositRecyclerView.setAdapter(depositAdapter);

        Button makeDepositBtn = (Button) rootView.findViewById(R.id.makeDepositButton);
        if (makeDepositBtn != null) {
            makeDepositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    depositAdapter.onMakeDepositClick(view,inflater);
                }
            });
        }

        return rootView;
    }
    public interface OnMakeDepositClickListener {
        void onMakeDepositClick(View view, LayoutInflater inflater);
    }
}
