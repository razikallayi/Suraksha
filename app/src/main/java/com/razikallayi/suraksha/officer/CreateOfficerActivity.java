package com.razikallayi.suraksha.officer;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import com.razikallayi.suraksha.utils.SmsUtils;

public class CreateOfficerActivity extends BaseActivity {

    private EditText txtName, txtPassword, txtUsername, txtMobile, txtAddress;
    private Switch switchIsAdmin;

    private CreateOfficerTask mCreateOfficerTask = null;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        NestedScrollView sv = (NestedScrollView) findViewById(R.id.create_officer_form);
        txtName = (EditText) sv.findViewById(R.id.txtName);
        txtUsername = (EditText) sv.findViewById(R.id.txtUsername);
        txtPassword = (EditText) sv.findViewById(R.id.txtPassword);
        txtMobile = (EditText) sv.findViewById(R.id.txtMobile);
        txtAddress = (EditText) sv.findViewById(R.id.txtAddress);
        switchIsAdmin = (Switch) sv.findViewById(R.id.switchIsAdmin);

        //Button Create Officer
        final Button mCreateOfficer = (Button) sv.findViewById(R.id.btnCreateOfficer);
        mCreateOfficer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()){   //no errors in input
                    mCreateOfficer.setEnabled(false);
                    Officer officer = getOfficerDetailsFromInput();
                    //Add the member to database
                    mCreateOfficerTask = new CreateOfficerTask(officer);
                    //Input to database using asyncTask. Which means run in background thread.
                    mCreateOfficerTask.execute((Void) null);
                }


            }
        });
    }

    private boolean validateInputs(){
        String password = txtPassword.getText().toString();
        if (TextUtils.isEmpty(txtName.getText().toString().trim())) {
            txtName.setError(getString(R.string.name_is_required));
            txtName.requestFocus();
            return false;
        }
        if (!TextUtils.isEmpty(txtMobile.getText().toString().trim())
                && !SmsUtils.isValidMobileNumber(txtMobile.getText().toString().trim())) {
            txtMobile.setError(getString(R.string.invalid_mobile_number));
            txtMobile.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(txtUsername.getText().toString().trim())) {
            txtUsername.setError(getString(R.string.username_is_required));
            txtUsername.requestFocus();
            return false;
        }
        if (!TextUtils.isEmpty(txtUsername.getText().toString().trim())) {
            Cursor cursor = getApplicationContext().getContentResolver().query(
                    SurakshaContract.OfficerEntry.buildCheckOfficerExistUri(
                            txtUsername.getText().toString().trim().toUpperCase()),null,null,null,null);
            if (cursor.getCount() > 0) {
                txtUsername.setError(getString(R.string.username_already_exist));
                txtUsername.requestFocus();
                return false;
            }
            cursor.close();
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.password_is_required));
            txtPassword.requestFocus();
            return false;
        }
        if (password.length() < 4) {
            txtPassword.setError(getString(R.string.pin_should_be_minimum_4_digits));
            txtPassword.requestFocus();
            return false;
        }
        if (!TextUtils.isDigitsOnly(password)) {
            txtPassword.setError(getString(R.string.pin_should_be_a_number));
            txtPassword.requestFocus();
            return false;
        }
        return true;
    }


    private Officer getOfficerDetailsFromInput() {
        //EditText Fields
        String name = txtName.getText().toString().trim();
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString();
        String mobile = txtMobile.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        boolean isAdmin = switchIsAdmin.isChecked();

        Officer officer = new Officer(getApplicationContext(), name, mobile, username, password, address, isAdmin);
        return officer;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateOfficerTask extends AsyncTask<Void, Void, Boolean> {
        private final Officer mOfficer;

        CreateOfficerTask(Officer officer) {
            this.mOfficer = officer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Save Member
            ContentValues values = Officer.getOfficerContentValues(mOfficer);
            getApplicationContext().getContentResolver().insert(SurakshaContract.OfficerEntry.CONTENT_URI, values);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mCreateOfficerTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), getString(R.string.officer_creation_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), OfficerListActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot create officer. ", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateOfficerTask = null;

        }
    }
}
