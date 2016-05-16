package com.razikallayi.suraksha.officer;


import android.content.ContentValues;
import android.content.Intent;
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

public class CreateOfficerActivity extends BaseActivity {

    private EditText txtName,txtPassword, txtUsername, txtMobile, txtAddress;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        NestedScrollView sv = (NestedScrollView) findViewById(R.id.create_officer_form);
        txtName             = (EditText) sv.findViewById(R.id.txtName);
        txtUsername            = (EditText) sv.findViewById(R.id.txtUsername);
        txtPassword           = (EditText) sv.findViewById(R.id.txtPassword);
        txtMobile           = (EditText) sv.findViewById(R.id.txtMobile);
        txtAddress          = (EditText) sv.findViewById(R.id.txtAddress);
        switchIsAdmin          = (Switch) sv.findViewById(R.id.switchIsAdmin);

        //Button Create Officer
        final Button mCreateOfficer = (Button) sv.findViewById(R.id.btnCreateOfficer);
        mCreateOfficer.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateOfficerTask extends AsyncTask<Void, Void, Boolean> {
        private final Officer mOfficer;

        CreateOfficerTask(Officer officer){
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
                Intent intent = new Intent(getApplicationContext(),OfficerListActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Cannot create officer. " , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateOfficerTask = null;

        }
    }
}
