package com.razikallayi.suraksha.account;

import android.content.Context;
import android.os.Bundle;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.CalendarUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 17-06-2016 01:51.
 */
public class MakeDepositActivity extends BaseActivity {
    public static final String ARG_ACCOUNT_NUMBER = "account_number";
    private int accountNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountNo = getIntent().getIntExtra(ARG_ACCOUNT_NUMBER,-1);
        Context context = getApplicationContext();

        List<Transaction> mDepositedTxnList = Member.fetchDeposits(context,String.valueOf(accountNo));

        Calendar depositMonth = CalendarUtils.getInstance();
        if (mDepositedTxnList.isEmpty()) {
            depositMonth = CalendarUtils.getSurakshaStartDate();
        } else {
            Transaction month = mDepositedTxnList.get(0);
            depositMonth.setTimeInMillis(month.getDefinedDepositMonth());
            depositMonth.add(Calendar.MONTH, 1);
        }
        setContentView(R.layout.deposit_make_activity);

//        builder.setMessage("Deposit " + Utility.formatAmountInRupees(context,
//                Utility.getMonthlyDepositAmount()) + " in account " + mAccount.getAccountNumber() + " ?");
//        builder.setTitle(CalendarUtils.readableDepositMonth(depositMonth));

//
//        String remarks = ((EditText) remarks_dialog_content.findViewById(R.id.remarksEditText)).getText().toString();
//        Transaction txn = makeDeposit(nextDepositMonth.getTimeInMillis(), remarks);
//        if (SmsUtils.smsEnabledAfterDeposit(remarks_dialog_content.getContext())) {
//            Member member = mAccount.getMember();
//            String phoneNumber = member.getMobile();
//            String message = member.getName() + ", your suraksha account " + mAccount.getAccountNumber()
//                    + " is credited with a deposit of " + Utility.formatAmountInRupees(remarks_dialog_content.getContext(), txn.getAmount())
//                    + " for the month " + CalendarUtils.readableDepositMonth(txn.getDefinedDepositMonth());
//            boolean result = SmsUtils.sendSms(message, phoneNumber);
//            if (result) {
//                Toast.makeText(remarks_dialog_content.getContext(), "SMS sent to " + member.getName(), Toast.LENGTH_SHORT).show();
//            }
//        }
    }
}
