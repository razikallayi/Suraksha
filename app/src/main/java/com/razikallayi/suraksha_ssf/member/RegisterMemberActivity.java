package com.razikallayi.suraksha_ssf.member;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.razikallayi.suraksha_ssf.BaseActivity;
import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.data.SurakshaContract;
import com.razikallayi.suraksha_ssf.txn.Transaction;
import com.razikallayi.suraksha_ssf.utils.AuthUtils;
import com.razikallayi.suraksha_ssf.utils.ImageUtils;
import com.razikallayi.suraksha_ssf.utils.SmsUtils;
import com.razikallayi.suraksha_ssf.utils.Utility;

import java.util.concurrent.ExecutionException;

public class RegisterMemberActivity extends BaseActivity {

    public static final int AVATAR_IMAGE_SIZE_IN_PIXEL = 500;
    //Intent to pick Contact
    private static final int PICK_CONTACT_REQUEST = 1;
    //Intent to pick avatar from gallery
    private static final int CHOOSE_AVATAR_REQUEST = 2;
    private EditText txtName, txtAlias, txtFather, txtSpouse, txtOccupation, txtAge, txtMobile, txtAddress,
            txtNominee, txtAddressOfNominee, txtRemarks;

    private ImageView imageViewAvatar;
    private byte[] memberAvatar = null;
    private Spinner mRelationWithNomineeSpinner;
    private CheckBox isAcceptedTerms;
    private RegisterMemberTask mRegisterMemberTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_member);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setHomeActionContentDescription("Close");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //Enable full view scroll while soft keyboard is shown
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
//                          |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        NestedScrollView sv = findViewById(R.id.register_member_form);
        txtName = sv.findViewById(R.id.txtName);
        txtAlias = sv.findViewById(R.id.txtAlias);
        txtFather = sv.findViewById(R.id.txtFather);
        txtSpouse = sv.findViewById(R.id.txtSpouse);
        txtOccupation = sv.findViewById(R.id.txtOccupation);
        txtAge = sv.findViewById(R.id.txtAge);
        txtMobile = sv.findViewById(R.id.txtMobile);
        txtAddress = sv.findViewById(R.id.txtAddress);
        txtNominee = sv.findViewById(R.id.txtNominee);
        txtAddressOfNominee = sv.findViewById(R.id.txtAddressOfNominee);
        txtRemarks = sv.findViewById(R.id.txtRemarks);
        isAcceptedTerms = sv.findViewById(R.id.accept_terms);

        //Spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arrRelationWithNominee, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mRelationWithNomineeSpinner = sv.findViewById(R.id.spnRelationWithNominee);
        mRelationWithNomineeSpinner.setAdapter(adapter);

        txtNominee.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    textView.clearFocus();
                    mRelationWithNomineeSpinner.requestFocus();
                    mRelationWithNomineeSpinner.performClick();
                }
                txtAddressOfNominee.requestFocus();
                return true;
            }
        });


        TextView tvAccountNumber = sv.findViewById(R.id.tvAccountNumber);
        TextView tvRegistrationFee = sv.findViewById(R.id.tvRegistrationFee);


        tvAccountNumber.setText(String.valueOf(Member.generateAccountNumber(getApplicationContext())));
        tvRegistrationFee.setText(getString(R.string.format_rupees, Utility.getRegistrationFeeAmount()));

        imageViewAvatar = findViewById(R.id.imageviewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Intent intent = new Intent();
                //                intent.setType("image/*");
                //                intent.setAction(Intent.ACTION_GET_CONTENT);
                //                startActivityForResult(Intent.createChooser(intent,
                // "Select Member Image"), CHOOSE_AVATAR_REQUEST);

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        CHOOSE_AVATAR_REQUEST);
            }
        });
        imageViewAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageViewAvatar.setImageResource(Member.DEFAULT_AVATAR);
                memberAvatar = null;
                return true;
            }
        });


        final Snackbar snackbar = Snackbar.make(sv, "Please accept and terms and conditions",
                Snackbar.LENGTH_LONG).setAction("Action", null);
        isAcceptedTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    snackbar.dismiss();
                }
            }
        });

        //Button Add Member
        final Button mAddMemberButton = sv.findViewById(R.id.btnAddMember);
        mAddMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating name column
                if (TextUtils.isEmpty(txtName.getText().toString().trim())) {
                    txtName.setError(getString(R.string.name_is_required));
                    txtName.requestFocus();
                } else if (!TextUtils.isEmpty(txtMobile.getText().toString().trim()) && !SmsUtils.isValidMobileNumber(txtMobile.getText().toString().trim())) {
                    txtMobile.setError(getString(R.string.invalid_mobile_number));
                    txtMobile.requestFocus();
                } else if (!isAcceptedTerms.isChecked()) {
                    snackbar.show();
                } else {   //no errors in input
                    mAddMemberButton.setEnabled(false);
                    Member member = getMemberDetailsFromInput();
                    //Add the member to database
                    mRegisterMemberTask = new RegisterMemberTask(member);
                    //Input to database using asyncTask. Which means run in background thread.
                    mRegisterMemberTask.execute((Void) null);
                }


            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void pickContact() {
        //Create an intent to pick contact
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            String[] contactsProjection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
            Cursor cursor = getContentResolver().query(data.getData(), contactsProjection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexPhone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    String name = cursor.getString(indexName);
                    String phone = cursor.getString(indexPhone);
                    String photo = cursor.getString((indexPhoto));

                    txtName.setText(name);
                    txtAlias.setText(name);
                    txtMobile.setText(phone);

                    if (photo != null) {
                        Uri uriContactPhoto = Uri.parse(photo);
                        saveAvatarTask t = new saveAvatarTask(this);
                        t.execute(uriContactPhoto);
                    } else {
                        if (imageViewAvatar != null) {
                            imageViewAvatar.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                    Member.DEFAULT_AVATAR, null));
                        }
                    }
                }
                cursor.close();
            }
        }

        if (resultCode == RESULT_OK && requestCode == CHOOSE_AVATAR_REQUEST) {
            Uri uriAvatar = data.getData();
            if (uriAvatar != null) {
                //Set it in the ImageView
                saveAvatarTask t = new saveAvatarTask(this);
                t.execute(uriAvatar);
            }
        }
    }

    private Member getMemberDetailsFromInput() {
        //Radio Button
        // get selected radio button from radioGroup
        RadioGroup mGenderRadioGroup = findViewById(R.id.rgpGender);
        // find the radio button by returned id
        RadioButton mSelectedGenderRadioButton = findViewById(mGenderRadioGroup.getCheckedRadioButtonId());

        //EditText Fields
        String name = txtName.getText().toString().trim();
        String alias = txtAlias.getText().toString().trim();
        String gender = mSelectedGenderRadioButton.getText().toString();
        String father = txtFather.getText().toString().trim();
        String spouse = txtSpouse.getText().toString().trim();
        String occupation = txtOccupation.getText().toString().trim();
        String age = txtAge.getText().toString().trim();
        String mobile = txtMobile.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        String nominee = txtNominee.getText().toString().trim();
        String addressOfNominee = txtAddressOfNominee.getText().toString().trim();
        String relationWithNominee = mRelationWithNomineeSpinner.getSelectedItem().toString();
        String remarks = txtRemarks.getText().toString().trim();
        if (relationWithNominee.equals(mRelationWithNomineeSpinner.getItemAtPosition(0))) {
            relationWithNominee = "";
        }
        Member member = new Member(getApplicationContext(), name, alias, gender, father, spouse,
                occupation, age, mobile, address, nominee, relationWithNominee, addressOfNominee, remarks);
        if (memberAvatar != null) {
            member.setAvatar(memberAvatar);
        }
        else{
            member.setAvatar(null);
        }
        return member;
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Any unsaved data will be removed. Are you sure want to close?")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Don't Close", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.base, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_member_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            showExitConfirmationDialog();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.pick_from_contacts_register_member) {
            pickContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegisterMemberTask extends AsyncTask<Void, Void, Boolean> {
        private final Member mMember;

        RegisterMemberTask(Member member) {
            this.mMember = member;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Generate Account Number
            int accountNumber = Member.generateAccountNumber(getApplicationContext());

            //Save Member
            mMember.setAccountNo(accountNumber);
            ContentValues values = Member.getMemberContentValues(mMember);
            getApplicationContext().getContentResolver().insert(
                    SurakshaContract.MemberEntry.CONTENT_URI, values);

            //Save Registration Fee
            Transaction txnRegistrationFee = new Transaction(getApplicationContext(), accountNumber,
                    Utility.getRegistrationFeeAmount(), SurakshaContract.TxnEntry.RECEIPT_VOUCHER,
                    SurakshaContract.TxnEntry.REGISTRATION_FEE_LEDGER, "Registration Fee",
                    AuthUtils.getAuthenticatedOfficerId(getApplicationContext()));
            values = Transaction.getTxnContentValues(txnRegistrationFee);
            getApplicationContext().getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);

            if (SmsUtils.smsEnabledAfterRegistration(getApplicationContext())) {
                String phoneNumber = mMember.getMobile();
                String message = mMember.getName() + ", " + getResources().getString(R.string.member_registered_sms)
                        + " Your account number is " + accountNumber;
                SmsUtils.sendSms(message, phoneNumber);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mRegisterMemberTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "SMS sent to " + mMember.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RegisterMemberActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Insertion Failed. ", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterMemberTask = null;

        }
    }

    private class saveAvatarTask extends AsyncTask<Uri, Void, Bitmap> {
        Context mContext;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.avatarProgress);

        public saveAvatarTask(Context context) {
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(Uri... uriAvatar) {
            if (null == uriAvatar) {
                return null;
            }

            try {
                Bitmap bitmap = Glide.with(mContext)
                        .asBitmap()
                        .load(uriAvatar[0])
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(AVATAR_IMAGE_SIZE_IN_PIXEL, AVATAR_IMAGE_SIZE_IN_PIXEL)
                        .get();
                memberAvatar = ImageUtils.bitmapToByteArray(bitmap);
                return bitmap;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.GONE);
            imageViewAvatar.setClickable(true);
            imageViewAvatar.setImageBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageViewAvatar.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

}
