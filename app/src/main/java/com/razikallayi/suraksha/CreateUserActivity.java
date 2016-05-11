package com.razikallayi.suraksha;


import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.razikallayi.suraksha.data.SurakshaContract;

public class CreateUserActivity extends AppCompatActivity {

    private EditText txtName,txtPassword, txtUsername, txtMobile, txtAddress;
    private Switch switchIsAdmin;

    private CreateUserTask mCreateUserTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

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


        NestedScrollView sv = (NestedScrollView) findViewById(R.id.create_user_form);
        txtName             = (EditText) sv.findViewById(R.id.txtName);
        txtUsername            = (EditText) sv.findViewById(R.id.txtUsername);
        txtPassword           = (EditText) sv.findViewById(R.id.txtPassword);
        txtMobile           = (EditText) sv.findViewById(R.id.txtMobile);
        txtAddress          = (EditText) sv.findViewById(R.id.txtAddress);
        switchIsAdmin          = (Switch) sv.findViewById(R.id.switchIsAdmin);

        //Button Create User
        final Button mCreateUser = (Button) sv.findViewById(R.id.btnCreateUser);
        mCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating name column
                if (TextUtils.isEmpty(txtName.getText().toString())) {
                    txtName.setError(getString(R.string.name_is_required));
                    txtName.requestFocus();
                }else if (TextUtils.isEmpty(txtUsername.getText().toString())) {
                    txtUsername.setError(getString(R.string.username_is_required));
                    txtUsername.requestFocus();
                }else if (TextUtils.isEmpty(txtPassword.getText().toString())) {
                    txtPassword.setError(getString(R.string.password_is_required));
                    txtPassword.requestFocus();
                }
                else {   //no errors in input
                    mCreateUser.setEnabled(false);
                    User user = getUserDetailsFromInput();
                    //Add the member to database
                    mCreateUserTask = new CreateUserTask(user);
                    //Input to database using asyncTask. Which means run in background thread.
                    mCreateUserTask.execute((Void) null);
                }


            }
        });
    }


    private User getUserDetailsFromInput(){
        //EditText Fields
        String name                = txtName.getText().toString();
        String username            = txtUsername.getText().toString();
        int password               = Integer.parseInt(txtPassword.getText().toString());
        String mobile              = txtMobile.getText().toString();
        String address             = txtAddress.getText().toString();
        boolean isAdmin             = switchIsAdmin.isChecked();

        return new User(getApplicationContext(),name,mobile, username, password, address, isAdmin);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
        private final User mUser;

        CreateUserTask(User user){
            this.mUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Save Member
            ContentValues values = User.getUserContentValues(mUser);
            getApplicationContext().getContentResolver().insert(SurakshaContract.UserEntry.CONTENT_URI, values);
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mCreateUserTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), getString(R.string.user_creation_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),CreateUserActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Cannot create user. " , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateUserTask = null;

        }
    }
}
