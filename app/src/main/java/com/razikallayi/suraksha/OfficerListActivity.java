package com.razikallayi.suraksha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OfficerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(new OfficerFragment(), OfficerFragment.TAG).commit();
    }

//    @Override
//    public void onListFragmentInteraction(DummyContent.DummyItem item) {
//        Toast.makeText(OfficerListActivity.this, "Implement Method Here", Toast.LENGTH_SHORT).show();
//    }
}
