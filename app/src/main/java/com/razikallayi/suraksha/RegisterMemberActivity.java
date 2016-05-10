package com.razikallayi.suraksha;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.ImageUtils;
import com.razikallayi.suraksha.utils.Utility;

import java.util.concurrent.ExecutionException;

public class RegisterMemberActivity extends AppCompatActivity {

    //Intent to pick Contact
    private static final int PICK_CONTACT_REQUEST = 1;
    //Intent to pick avatar from gallery
    private static final int CHOOSE_AVATAR_REQUEST = 2;

    public static final int AVATAR_IMAGE_SIZE_IN_PIXEL = 720;

    private EditText txtName,txtAlias, txtFather, txtSpouse, txtOccupation, txtAge, txtMobile, txtAddress,
            txtNominee, txtAddressOfNominee, txtRemarks;

    private CheckBox chkRegistrationFee;

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

        NestedScrollView sv = (NestedScrollView) findViewById(R.id.register_member_form);
        txtName             = (EditText) sv.findViewById(R.id.txtName);
        txtAlias            = (EditText) sv.findViewById(R.id.txtAlias);
        txtFather           = (EditText) sv.findViewById(R.id.txtFather);
        txtSpouse           = (EditText) sv.findViewById(R.id.txtSpouse);
        txtOccupation       = (EditText) sv.findViewById(R.id.txtOccupation);
        txtAge              = (EditText) sv.findViewById(R.id.txtAge);
        txtMobile           = (EditText) sv.findViewById(R.id.txtMobile);
        txtAddress          = (EditText) sv.findViewById(R.id.txtAddress);
        txtNominee          = (EditText) sv.findViewById(R.id.txtNominee);
        txtAddressOfNominee = (EditText) sv.findViewById(R.id.txtAddressOfNominee);
        txtRemarks          = (EditText) sv.findViewById(R.id.txtRemarks);
        isAcceptedTerms     = (CheckBox) sv.findViewById(R.id.accept_terms);

        //Spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arrRelationWithNominee, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mRelationWithNomineeSpinner = (Spinner) sv.findViewById(R.id.spnRelationWithNominee);
        mRelationWithNomineeSpinner.setAdapter(adapter);

        TextView tvAccountNumber = (TextView) sv.findViewById(R.id.tvAccountNumber);
        TextView tvRegistrationFee = (TextView) sv.findViewById(R.id.tvRegistrationFee);

        chkRegistrationFee = (CheckBox) sv.findViewById(R.id.chkRegistrationFee);


        tvAccountNumber.setText(String.valueOf(Account.generateAccountNumber(getApplicationContext())));
        tvRegistrationFee.setText(getString(R.string.format_rupees, Utility.getRegistrationFeeAmount()));
        //tvPendingDeposits.setText(getString(R.string.format_rupees, Utility.getOpeningDepositAmount()));

//         RecyclerView rvPendingDeposit= (RecyclerView) sv.findViewById(R.id.pending_deposit_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        rvPendingDeposit.setHasFixedSize(true);

        // use a linear layout manager
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        rvPendingDeposit.setLayoutManager(layoutManager);

//        List<Calendar> pendingMonthsList = Utility.getPendingDepositMonths(null);
        // specify an adapter (see also next example)
//        PendingDepositAdapter pendingDepositAdapter = new PendingDepositAdapter(pendingMonthsList);
//        rvPendingDeposit.setAdapter(pendingDepositAdapter);

        //Set Total Payable amount at time of registration
//        TextView tvTotal = (TextView) sv.findViewById(R.id.tvTotalRegistration);
//        double amt = (pendingMonthsList.size() * Utility.getMonthlyDepositAmount())+Utility.getRegistrationFeeAmount();
//        tvTotal.setText("TOTAL : "+Utility.formatAmountInRupees(getApplicationContext(),amt));


        imageViewAvatar = (ImageView) findViewById(R.id.imageviewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Intent intent = new Intent();
                //                intent.setType("image/*");
                //                intent.setAction(Intent.ACTION_GET_CONTENT);
                //                startActivityForResult(Intent.createChooser(intent,
                // "Select Member Image"), CHOOSE_AVATAR_REQUEST);

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), CHOOSE_AVATAR_REQUEST);
            }
        });



        final Snackbar snackbar = Snackbar.make(sv, "Please accept and terms and conditions", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        isAcceptedTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    snackbar.dismiss();
                }
            }
        });

        //Button Add Member
        final Button mAddMemberButton = (Button) sv.findViewById(R.id.btnAddMember);
        mAddMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating name column
                if (TextUtils.isEmpty(txtName.getText().toString())) {
                    txtName.setError(getString(R.string.name_is_required));
                    txtName.requestFocus();
                } else if (!isAcceptedTerms.isChecked()) {
                    snackbar.show();
                    //isAcceptedTerms.setError("Please accept and pay registration fee of ₹"+REGISTRATION_FEE_AMOUNT);
                } else if (!chkRegistrationFee.isChecked()) {

                    Snackbar.make(v, "Please pay and tick the registration fee", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //isAcceptedTerms.setError("Please accept and pay registration fee of ₹"+REGISTRATION_FEE_AMOUNT);
                }
//                else if (!chkPendingDeposits.isChecked()) {
//                    Snackbar.make(v, "Please pay and tick the pending deposits", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
                else {   //no errors in input
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

    private void pickContact(){
    //Create an intent to pick contact
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST){
            String[] contactsProjection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
            Cursor cursor = getContentResolver().query(data.getData(),contactsProjection,null,null,null);
            if (cursor != null) {
                if (cursor.moveToFirst()){
                    int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexPhone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexPhoto = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    String name = cursor.getString(indexName);
                    String phone = cursor.getString(indexPhone);
                    String photo = cursor.getString((indexPhoto));

                    txtName.setText(name);
                    txtAlias.setText(name);
                    txtMobile.setText(phone);

                    if(photo !=null) {
                        Uri uriContactPhoto = Uri.parse(photo);
                        saveAvatarTask t = new saveAvatarTask(this);
                        t.execute(uriContactPhoto);
                    }
                    else {
                        if(imageViewAvatar!=null) {
                            imageViewAvatar.setImageDrawable(ResourcesCompat.getDrawable(getResources(), Member.DEFAULT_AVATAR, null));
                        }
                    }
                }
                cursor.close();
            }
        }

        if(resultCode == RESULT_OK  && requestCode == CHOOSE_AVATAR_REQUEST) {
            Uri uriAvatar = data.getData();
            if (uriAvatar != null) {
                //Set it in the ImageView
                saveAvatarTask t = new saveAvatarTask(this);
                t.execute(uriAvatar);
            }
        }
    }

    private Member getMemberDetailsFromInput(){
        //Radio Button
        // get selected radio button from radioGroup
        RadioGroup mGenderRadioGroup = (RadioGroup) findViewById(R.id.rgpGender);
        // find the radio button by returned id
        RadioButton mSelectedGenderRadioButton = (RadioButton) findViewById(mGenderRadioGroup.getCheckedRadioButtonId());

        //EditText Fields
        String name                = txtName.getText().toString();
        String alias               = txtAlias.getText().toString();
        String gender              = mSelectedGenderRadioButton.getText().toString();
        String father              = txtFather.getText().toString();
        String spouse              = txtSpouse.getText().toString();
        String occupation          = txtOccupation.getText().toString();
        String age                 = txtAge.getText().toString();
        String mobile              = txtMobile.getText().toString();
        String address             = txtAddress.getText().toString();
        String nominee             = txtNominee.getText().toString();
        String addressOfNominee    = txtAddressOfNominee.getText().toString();
        String relationWithNominee = mRelationWithNomineeSpinner.getSelectedItem().toString();
        String remarks             = txtRemarks.getText().toString();
        if(relationWithNominee.equals(mRelationWithNomineeSpinner.getItemAtPosition(0))){
            relationWithNominee = "";
        }
        Member member =  new Member(getApplicationContext(),name, alias, gender, father, spouse,
                occupation, age, mobile, address, nominee, relationWithNominee, addressOfNominee,remarks);
        if(memberAvatar!=null) {
            member.setAvatar(memberAvatar);
        }
        return member;
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog(){
        new AlertDialog.Builder(this)
                .setMessage("Any unsaved data will be removed. Are you sure want to close?")
                .setPositiveButton("Close", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.base, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_member_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            return true;
        }

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

        RegisterMemberTask(Member member){
            this.mMember = member;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Save Member
            ContentValues values = Member.getMemberContentValues(mMember);
            getApplicationContext().getContentResolver().insert(SurakshaContract.MemberEntry.CONTENT_URI, values);

            //Save Account for member
            int accountNumber = Account.generateAccountNumber(getApplicationContext());
            Account account = new Account(mMember,Utility.getOpeningDepositAmount(),1);
            account.setAccountNumber(accountNumber);
            values = Account.getAccountContentValues(account);
            getApplicationContext().getContentResolver().insert(SurakshaContract.AccountEntry.CONTENT_URI, values);

            //Save Registration Fee
            Transaction txnRegistrationFee = new Transaction(accountNumber, Utility.getRegistrationFeeAmount(),SurakshaContract.TxnEntry.RECEIPT_VOUCHER,SurakshaContract.TxnEntry.REGISTRATION_FEE_LEDGER,"");
            values = Transaction.getTxnContentValues(txnRegistrationFee);
            getApplicationContext().getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean sendSms = prefs.getBoolean("enable_sms",false) && prefs.getBoolean("sms_after_registration",false);
            if(sendSms) {
                SmsManager sms = SmsManager.getDefault();
                String phoneNumber = "121";//mMember.getMobile();
                String message = getResources().getString(R.string.member_registered_sms)
                        + " Your account number is " + account.getAccountNumber();
                sms.sendTextMessage(phoneNumber, null, message, null, null);
            }


            //Save Monthly Deposit which is pending at time of registration
//            List<Calendar> pendingMonths = Utility.getPendingDepositMonths(null);
//            List<ContentValues> cv = new ArrayList<>();
//            for (int i = 0; i < pendingMonths.size(); i++) {
//                Transaction txnPendingMonth = new Transaction(accountNumber,
//                        Utility.getMonthlyDepositAmount(),
//                        SurakshaContract.TxnEntry.RECEIPT_VOUCHER,
//                        SurakshaContract.TxnEntry.DEPOSIT_LEDGER,
//                        "Deposited at time of registration");
//                txnPendingMonth.setDefinedDepositDate(pendingMonths.get(i).getTimeInMillis());
//                if (txnPendingMonth.getAmount() > 0){
//                    values = Transaction.getTxnContentValues(txnPendingMonth);
//                    cv.add(values);
//                    //getApplicationContext().getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);
//                }
//            }
//            ContentValues[] cvArray = cv.toArray(new ContentValues[cv.size()]);
//            getApplicationContext().getContentResolver().bulkInsert(SurakshaContract.TxnEntry.CONTENT_URI, cvArray);

            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mRegisterMemberTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),RegisterMemberActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Insertion Failed. " , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterMemberTask = null;

        }
    }

    private class saveAvatarTask extends AsyncTask<Uri,Void,Bitmap> {
        Context mContext;
        ProgressBar progressBar =(ProgressBar)findViewById(R.id.avatarProgress);
        public saveAvatarTask(Context context) {
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(Uri... uriAvatar) {

            try {
                Bitmap bitmap = Glide.with(mContext)
                        .load(uriAvatar[0])
                        .asBitmap()
                        .skipMemoryCache( true)
                        .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                        .into(AVATAR_IMAGE_SIZE_IN_PIXEL,AVATAR_IMAGE_SIZE_IN_PIXEL)
                        .get();
                memberAvatar = ImageUtils.bitmapToByteArray(bitmap);
                return bitmap;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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
