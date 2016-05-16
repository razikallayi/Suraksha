package com.razikallayi.suraksha;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.utils.LoginUtils;
import com.razikallayi.suraksha.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private TextView mUsernameTv;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String mRecentOfficer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
        setContentView(R.layout.activity_login);

        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mRecentOfficer = SettingsUtils.getRecentOfficer(getApplicationContext());
        if(null == mRecentOfficer) {
            populateAutoComplete();
        }
        else {
            mUsernameView.setVisibility(View.GONE);
            mUsernameTv = (TextView) findViewById(R.id.tvUsername);
            mUsernameTv.setText(mRecentOfficer);
            mUsernameTv.setVisibility(View.VISIBLE);
            Button mSignOutButton = (Button) findViewById(R.id.sign_out_button);
            mSignOutButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginUtils.logout(getApplicationContext());
                    setResult(RESULT_CANCELED);
                }
            });
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(mPasswordView.getText().length() >= 4){
                        return false;
                    }
                }
                if(event.getAction() == KeyEvent.ACTION_UP){
                    if(mPasswordView.getText().length() == 4){
                        attemptLogin();
                    }
                }
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        if(null == mRecentOfficer) {
            // Reset errors.
            mUsernameView.setError(null);
        }
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username=null;
        if(null == mRecentOfficer) {
            username = mUsernameView.getText().toString();
        }else {
            username = mRecentOfficer;
        }

        String pin = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (pin.length()<4) {
            mPasswordView.setError(getString(R.string.pin_should_be_minimum_4_digits));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid pin, if the user entered one.
        if (TextUtils.isEmpty(pin)) {
            mPasswordView.setError(getString(R.string.pin_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.username_is_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(username, pin);
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                SurakshaContract.OfficerEntry.CONTENT_URI, OfficerQuery.PROJECTION,
                null,
                null,
                SurakshaContract.OfficerEntry.COLUMN_USERNAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> usernameList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            usernameList.add(cursor.getString(OfficerQuery.COL_USERNAME));
            cursor.moveToNext();
        }
        addUsernamesToAutoComplete(usernameList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private void addUsernamesToAutoComplete(List<String> usernameCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, usernameCollection);
        mUsernameView.setAdapter(adapter);
    }

    private interface OfficerQuery {
        String[] PROJECTION = {
                SurakshaContract.OfficerEntry.COLUMN_USERNAME
        };
        int COL_USERNAME = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPin;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPin = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return Officer.authenticate(getApplicationContext(), mUsername, mPin);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                LoginUtils.login(getApplicationContext(),mUsername);
                setResult(RESULT_OK);
                finish();
            } else {
                LoginUtils.logout(getApplicationContext());
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

