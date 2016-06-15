package com.razikallayi.suraksha.account;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.SmsUtils;
import com.razikallayi.suraksha.utils.Utility;

public class CreateAccountActivity extends BaseActivity {

    private Member mMember;

    private CreateAccountTask mCreateAccountTask;

    private CheckBox isAcceptedTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_create_activity);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Account");
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        View layoutCreateAccount = findViewById(R.id.layoutCreateAccount);
        TextView tvMemberName = (TextView) layoutCreateAccount.findViewById(R.id.tvMemberName);
        TextView tvMemberAddress = (TextView) layoutCreateAccount.findViewById(R.id.tvMemberAddress);
        TextView tvAccountNumber = (TextView) layoutCreateAccount.findViewById(R.id.tvAccountNumber);
        TextView tvRegistrationFee = (TextView) layoutCreateAccount.findViewById(R.id.tvRegistrationFee);
        isAcceptedTerms = (CheckBox) layoutCreateAccount.findViewById(R.id.accept_terms);

        long memberId = getIntent().getLongExtra(AccountListFragment.ARG_MEMBER_ID, 0);
        mMember = Member.getMemberFromId(getApplicationContext(), memberId);
        tvMemberName.setText(mMember.getName());
        tvMemberAddress.setText(mMember.getAddress());
        tvAccountNumber.setText(String.valueOf(Account.generateAccountNumber(getApplicationContext())));
        tvRegistrationFee.setText(getString(R.string.format_rupees, Utility.getRegistrationFeeAmount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.base, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_account_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_account) {
            createAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAccount() {
        View v = findViewById(R.id.layoutCreateAccount);
        if (!isAcceptedTerms.isChecked()) {
            Snackbar.make(v, "Please accept and terms and conditions", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {   //no errors in input
            mCreateAccountTask = new CreateAccountTask(mMember);
            mCreateAccountTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateAccountTask extends AsyncTask<Void, Void, Boolean> {
        private final Member mMember;

        CreateAccountTask(Member member) {
            this.mMember = member;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();

            Account account = new Account(mMember, Utility.getOpeningDepositAmount(), true);
            account.setAccountNumber(Account.generateAccountNumber(getApplicationContext()));
            Transaction registrationFeeTxn = new Transaction(getApplicationContext(),account.getAccountNumber(),
                    Utility.getRegistrationFeeAmount(), SurakshaContract.TxnEntry.RECEIPT_VOUCHER,
                    SurakshaContract.TxnEntry.REGISTRATION_FEE_LEDGER, "New Account",
                    AuthUtils.getAuthenticatedOfficerId(getApplicationContext()));

            ContentValues values = Account.getAccountContentValues(account);
            contentResolver.insert(SurakshaContract.AccountEntry.CONTENT_URI, values);

            values = Transaction.getTxnContentValues(registrationFeeTxn);
            contentResolver.insert(SurakshaContract.TxnEntry.CONTENT_URI, values);

            if (SmsUtils.smsEnabledAfterCreateAccount(getApplicationContext())) {
                String phoneNumber = mMember.getMobile();
                String message = getResources().getString(R.string.account_created_sms) + " Your account number is " + account.getAccountNumber();
                SmsUtils.sendSms(message,phoneNumber);
                Toast.makeText(getApplicationContext(), "SMS sent to "+mMember.getName(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mCreateAccountTask = null;
            if (success) {
                setResult(RESULT_OK);
                finish();
                Toast.makeText(getApplicationContext(), "Account Created Successfully. ", Toast.LENGTH_LONG).show();
            } else {
                setResult(RESULT_CANCELED);
                Toast.makeText(getApplicationContext(), "Account Creation Failed. ", Toast.LENGTH_LONG).show();
            }
        }
    }
}


