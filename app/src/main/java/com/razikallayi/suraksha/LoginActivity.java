package com.razikallayi.suraksha;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.razikallayi.suraksha.officer.Officer;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.SettingsUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
//        implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mRecentOfficer = null;
    private boolean isBackPressed = false;
    private ImageView logo;
    private ImageView logoTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
        setContentView(R.layout.activity_login);

        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        logo = findViewById(R.id.logo);
        logoTagline = findViewById(R.id.logoTagline);

        mRecentOfficer = SettingsUtils.getOfficerUsername(getApplicationContext());
        if (null != mRecentOfficer) {
            mUsernameView.setVisibility(View.GONE);
            TextView mUsernameTv = findViewById(R.id.tvUsername);
            mUsernameTv.setText(mRecentOfficer);
            mUsernameTv.setContentDescription(mRecentOfficer);
            //((View)(mUsernameTv.getParent())).setContentDescription(mRecentOfficer);
            mUsernameTv.setVisibility(View.VISIBLE);
            Button mSignOutButton = findViewById(R.id.sign_out_button);
            mSignOutButton.setVisibility(View.VISIBLE);

            mSignOutButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRecentOfficer = null;
                    AuthUtils.logout(getApplicationContext());
                    recreate();
                }
            });
        }

        mPasswordView.setTransformationMethod(new HiddenPassTransformationMethod());
        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (mPasswordView.getText().length() == 4 && mAuthTask == null) {
                        attemptLogin();
                        mPasswordView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.sign_in_button || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ValueAnimator animator = ValueAnimator.ofFloat(-300, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translateValue = (float) animation.getAnimatedValue();
                logo.setTranslationX(translateValue);
                logoTagline.setTranslationX(translateValue);
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2000);
        animator.start();

    }

    private void mockLogin(String username, String password, int delayInMillisecond) {
        mUsernameView.setText(username);
        mPasswordView.setText(password);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                attemptLogin();
            }
        }, delayInMillisecond);

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

        if (null == mRecentOfficer) {
            // Reset errors.
            mUsernameView.setError(null);
        }
        mPasswordView.setError(null);

        //get username from input text
        String username = mRecentOfficer;
        if (username == null) {
            username = mUsernameView.getText().toString();
        }

        String pin = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (pin.length() < 4) {
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPin;

        UserLoginTask(String username, String password) {
            mUsername = username.toUpperCase();
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
                setResult(RESULT_OK);

                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }
}

