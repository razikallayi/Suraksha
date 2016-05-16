package com.razikallayi.suraksha.officer;


import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;

public class EditOfficerActivity extends BaseActivity {
    public static final String ARG_OFFICER_ID = "officer_id";

    private EditText txtName,txtPassword, txtUsername, txtMobile, txtAddress;
    private Switch switchIsAdmin;

    private EditOfficerTask mEditOfficerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_create_officer_activity);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setHomeActionContentDescription("Close");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Enable full view scroll while soft keyboard is shown
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        NestedScrollView sv = (NestedScrollView) findViewById(R.id.create_officer_form);
        txtName            = (EditText) sv.findViewById(R.id.txtName);
        txtUsername        = (EditText) sv.findViewById(R.id.txtUsername);
        txtPassword        = (EditText) sv.findViewById(R.id.txtPassword);
        txtMobile          = (EditText) sv.findViewById(R.id.txtMobile);
        txtAddress         = (EditText) sv.findViewById(R.id.txtAddress);
        switchIsAdmin      = (Switch) sv.findViewById(R.id.switchIsAdmin);

        setOfficerDetailsFromId(getIntent().getLongExtra(ARG_OFFICER_ID,-1));

        //Button Create Officer
        final Button mEditOfficer = (Button) sv.findViewById(R.id.btnCreateOfficer);
        mEditOfficer.setText(getString(R.string.update));
        mEditOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtPassword.getText().toString();
                //Validating name column
                if (TextUtils.isEmpty(txtName.getText().toString())) {
                    txtName.setError(getString(R.string.name_is_required));
                    txtName.requestFocus();
                }else if (TextUtils.isEmpty(txtUsername.getText().toString())) {
                    txtUsername.setError(getString(R.string.username_is_required));
                    txtUsername.requestFocus();
                }else if (TextUtils.isEmpty(password)) {
                    txtPassword.setError(getString(R.string.password_is_required));
                    txtPassword.requestFocus();
                }else if (password.length()<4) {
                    txtPassword.setError(getString(R.string.pin_should_be_minimum_4_digits));
                    txtPassword.requestFocus();
                }else if (!TextUtils.isDigitsOnly(password)) {
                    txtPassword.setError(getString(R.string.pin_should_be_a_number));
                    txtPassword.requestFocus();
                }
                else {   //no errors in input
                    mEditOfficer.setEnabled(false);
                    Officer officer = getOfficerDetailsFromInput();
                    //Add the member to database
                    mEditOfficerTask = new EditOfficerTask(officer);
                    //Input to database using asyncTask. Which means run in background thread.
                    mEditOfficerTask.execute((Void) null);
                }


            }
        });
    }


    private Officer getOfficerDetailsFromInput(){
        //EditText Fields
        String name                = txtName.getText().toString();
        String username            = txtUsername.getText().toString();
        String password            = txtPassword.getText().toString();
        String mobile              = txtMobile.getText().toString();
        String address             = txtAddress.getText().toString();
        boolean isAdmin             = switchIsAdmin.isChecked();

        return new Officer(getApplicationContext(),name,mobile, username, password, address, isAdmin);
    }

    private Officer setOfficerDetailsFromId(long officerId) {
        if (officerId == -1) {
            return null;
        }
        Officer officer = Officer.getOfficerFromId(getApplicationContext(), officerId);
        txtName.setText(officer.getName());
        txtMobile.setText(officer.getMobile());
        txtUsername.setText(officer.getUsername());
        txtPassword.setText(String.valueOf(officer.getPassword()));
        txtAddress.setText(officer.getAddress());
        switchIsAdmin.setChecked(officer.isAdmin());

        return officer;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class EditOfficerTask extends AsyncTask<Void, Void, Boolean> {
        private final Officer mOfficer;

        EditOfficerTask(Officer officer){
            this.mOfficer = officer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Save Officer
            ContentValues values = Officer.getOfficerContentValues(mOfficer);
            getApplicationContext().getContentResolver().update(
                    SurakshaContract.OfficerEntry.CONTENT_URI, values,
                    SurakshaContract.OfficerEntry._ID+"=?",
                    new String[]{String.valueOf(getIntent().getLongExtra(ARG_OFFICER_ID, -1))});
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mEditOfficerTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), getString(R.string.officer_updated_successfully), Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Cannot update officer details. " , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mEditOfficerTask = null;

        }
    }
}
