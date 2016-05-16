package com.razikallayi.suraksha.member;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.razikallayi.suraksha.BaseActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.account.AccountListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An activity representing a single Member detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MemberListActivity}.
 */
public class MemberDetailActivity extends BaseActivity {

    public static final String ARG_MEMBER_NAME = "member_name";
    public static final String ARG_MEMBER_ID = "member_id";

    private String mMemberName;
    private long mMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (null != savedInstanceState) {
            mMemberId = savedInstanceState.getLong(ARG_MEMBER_ID);
            mMemberName = savedInstanceState.getString(ARG_MEMBER_NAME);

        }else{
            mMemberId = getIntent().getLongExtra(ARG_MEMBER_ID,-1);
            mMemberName = getIntent().getStringExtra(ARG_MEMBER_NAME);
        }
        
        //Load Avatar
        LoadAvatarTask loadAvatarTask = new LoadAvatarTask();
        loadAvatarTask.execute(mMemberId);

        //Set Toolbar and load Member Name
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mMemberName);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.member_detail_container);
        if (viewPager != null) {
            setupViewPager(viewPager,mMemberId);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(ARG_MEMBER_ID, mMemberId);
        outState.putString(ARG_MEMBER_NAME, mMemberName);
        super.onSaveInstanceState(outState);
    }

    private void setupViewPager(ViewPager viewPager, long memberId) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        AccountListFragment accountListFragment = (AccountListFragment)
                getSupportFragmentManager().findFragmentByTag(AccountListFragment.TAG);
        if(null == accountListFragment) {
            Bundle arguments = new Bundle();
            //AccountList
            arguments.putLong(AccountListFragment.ARG_MEMBER_ID, memberId);
            accountListFragment = new AccountListFragment();
            accountListFragment.setArguments(arguments);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.account_list_container, accountListFragment)
//                .commit();
            adapter.addFragment(accountListFragment, "Accounts");
        }

        MemberDetailFragment memberDetailFragment = (MemberDetailFragment)
                getSupportFragmentManager().findFragmentByTag(MemberDetailFragment.TAG);
        if (memberDetailFragment == null) {

            Bundle arguments = new Bundle();
            //MemberDetails
            arguments.putLong(MemberDetailFragment.ARG_MEMBER_ID, memberId);
            memberDetailFragment = new MemberDetailFragment();
            memberDetailFragment.setArguments(arguments);
            adapter.addFragment(memberDetailFragment, "Personal");
            //        getSupportFragmentManager().beginTransaction()
            //                .add(R.id.member_detail_container, fragment)
            //                .commit();
        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_member_details, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_member) {
            Intent intent = new Intent(getApplicationContext(), EditMemberActivity.class);
            intent.putExtra(EditMemberActivity.ARG_MEMBER_ID, mMemberId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private class LoadAvatarTask extends AsyncTask<Long,Void,Member>{
        @Override
        protected Member doInBackground(Long[] memberId) {
            return Member.getMemberFromId(getApplicationContext(),memberId[0]);
        }
        @Override
        protected void onPostExecute(Member member) {
            super.onPostExecute(member);
            ImageView ivAvatar = (ImageView)findViewById(R.id.ivAvatarMemberDetailActivity);
            if (member.getAvatarDrawable() != null) {
                ivAvatar.setImageDrawable(member.getAvatarDrawable());
            }else{
                Random rnd = new Random();
                int Low = 50;
                int High = 200;

                ivAvatar.setImageDrawable(new ColorDrawable(Color.rgb(rnd.nextInt(High-Low) + Low, rnd.nextInt(High-Low) + Low, rnd.nextInt(High-Low) + Low)));
//                ivAvatar.setImageDrawable(new LetterAvatar(getApplicationContext(),
//                        R.color.blue, member.getName().substring(0, 1).toUpperCase(), 24));
            }
        }
    }


}