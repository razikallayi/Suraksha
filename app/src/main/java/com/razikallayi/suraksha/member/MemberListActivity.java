package com.razikallayi.suraksha.member;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;

/**
 * An activity representing a list of Members. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MemberDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MemberListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener
{

    public static final String TAG = MemberListActivity.class.getClass().getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private MemberListAdapter mMemberListAdapter;
    // The SearchView for doing filtering.
    private SearchView mSearchView;

    // If non-null, this is the current filter the user has provided.
    private String mCurFilter;


    private static final int MEMBER_LIST_LOADER = 0;

    private static final String[] MEMBER_COLUMNS = {
            SurakshaContract.MemberEntry.TABLE_NAME+"."+SurakshaContract.MemberEntry._ID,
            SurakshaContract.MemberEntry.COLUMN_NAME,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS,
            SurakshaContract.MemberEntry.COLUMN_AVATAR,
    };

    private static final int COL_MEMBER_ID = 0;
    private static final int COL_MEMBER_NAME = 1;
    private static final int COL_MEMBER_ADDRESS = 2;
    private static final int COL_MEMBER_AVATAR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the register member action
                    Intent intent = new Intent(getApplicationContext(), RegisterMemberActivity.class);
                    startActivity(intent);
                }
            });
        }

        // The MemberListAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mMemberListAdapter = new MemberListAdapter(getApplicationContext());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.member_list);

        mMemberListAdapter.setOnItemClickListener(new MemberListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long memberId, String memberName) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putLong(MemberDetailFragment.ARG_MEMBER_ID, memberId);
                    MemberDetailFragment fragment = new MemberDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.member_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MemberDetailActivity.class);
                    intent.putExtra(MemberDetailFragment.ARG_MEMBER_ID, memberId);
                    intent.putExtra(MemberDetailActivity.ARG_MEMBER_NAME, memberName);
                    startActivity(intent);
                }

            }
        });

        if (recyclerView != null) {
            recyclerView.setAdapter(mMemberListAdapter);
        }

        //setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.member_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        getSupportLoaderManager().initLoader(MEMBER_LIST_LOADER,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setOnQueryTextListener(this);
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        Bundle queryBundle = new Bundle();
        queryBundle.putString("query",mCurFilter);
        getSupportLoaderManager().restartLoader(MEMBER_LIST_LOADER, queryBundle, this);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    /*

        private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
            Cursor cursorMembers = getApplicationContext().getContentResolver().query(
                    SurakshaContract.MemberEntry.CONTENT_URI, MEMBER_COLUMNS, null, null,
                    SurakshaContract.MemberEntry.COLUMN_NAME.concat(" COLLATE NOCASE"));
            recyclerView.setAdapter(new MemberRecyclerViewAdapter(setListAdapter(cursorMembers)));
        }

        private ArrayList<Member> setListAdapter(Cursor cursor) {
            ArrayList<Member> listMember = new ArrayList<>();
            while(cursor.moveToNext()) {
                Member member = new Member();
                // The Cursor is now set to the right position
                member.setId(cursor.getLong(COL_MEMBER_ID));
                member.setName(cursor.getString(COL_MEMBER_NAME));
                member.setAddress(cursor.getString(COL_MEMBER_ADDRESS));
                member.setAvatar(cursor.getBlob(COL_MEMBER_AVATAR));
                member.fetchAccountNumbers(getApplicationContext());
                listMember.add(member);
            }
            cursor.close();
            return listMember;
        }
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            return new CursorLoader(getApplicationContext(),
                    SurakshaContract.MemberEntry.buildMemberJoinAccount(),
                    MEMBER_COLUMNS, SurakshaContract.MemberEntry.COLUMN_NAME
                    +" LIKE ? OR "+SurakshaContract.MemberEntry.COLUMN_ADDRESS
                    +" LIKE ? OR "+ SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER +"=?",
                    new String[]{String.valueOf("%"+args.get("query")+"%"),String.valueOf("%"+args.get("query")+"%"),String.valueOf(args.get("query"))},
                    SurakshaContract.MemberEntry.TABLE_NAME+"."+SurakshaContract.MemberEntry._ID +" COLLATE NOCASE");
        }
        else {
            return new CursorLoader(getApplicationContext(),
                    SurakshaContract.MemberEntry.CONTENT_URI,
                    MEMBER_COLUMNS, null, null,
                    SurakshaContract.MemberEntry._ID.concat(" COLLATE NOCASE"));
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMemberListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMemberListAdapter.swapCursor(null);
    }

/*
    public class MemberRecyclerViewAdapter
            extends RecyclerView.Adapter<MemberRecyclerViewAdapter.ViewHolder> {

        private List<Member> mMemberArray;

        public MemberRecyclerViewAdapter(List<Member> members) {
            mMemberArray = members;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.member_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mMemberArray.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mMember = mMemberArray.get(position);

            List<Integer> accountNumbers = holder.mMember.getAccountNumbers();
            holder.mAccountNumbersView.removeAllViews();
            int count = 0;
            for (int accountNumber: accountNumbers) {
                //Show maximum of 5 account numbers
//                if(++count>5){
//                    break;
//                }

                TextView tvAccountNumber = new TextView(getApplicationContext());
                tvAccountNumber.setText(String.valueOf(accountNumber));
                tvAccountNumber.setTextSize(COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_small));
                tvAccountNumber.setTypeface(null,Typeface.BOLD);

                tvAccountNumber.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                tvAccountNumber.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_circle,null));
                tvAccountNumber.setGravity(Gravity.CENTER);
                //set Padding
                int sidePaddingPixel = 2;
                int sidePaddingDp = (int)(sidePaddingPixel * getApplicationContext().getResources().getDisplayMetrics().density);
                LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(sidePaddingDp,sidePaddingDp,sidePaddingDp,sidePaddingDp);
                holder.mAccountNumbersView.addView(tvAccountNumber,params);
            }

            SetAvatarTask t = new SetAvatarTask(getApplicationContext(),holder);
            t.execute(holder.mMember);

            holder.mNameView.setText(holder.mMember.getName());
            holder.mAddressView.setText(holder.mMember.getAddress());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putLong(MemberDetailFragment.ARG_MEMBER_ID, holder.mMember.getId());
                        MemberDetailFragment fragment = new MemberDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.member_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MemberDetailActivity.class);
                        intent.putExtra(MemberDetailFragment.ARG_MEMBER_ID, holder.mMember.getId());
                        intent.putExtra(MemberDetailActivity.ARG_MEMBER_NAME, holder.mMember.getName());

                        context.startActivity(intent);
                    }
                }
            });


        }

        private class SetAvatarTask extends AsyncTask<Member,Void,Drawable> {
            Context mContext;
            ViewHolder holder;
            public SetAvatarTask(Context context,ViewHolder viewHolder) {
                mContext = context;
                holder = viewHolder;
            }

            @Override
            protected Drawable doInBackground(Member... members) {
                Drawable drawableAvatar = null;
                final int Low = 50;
                final int High = 200;
                Random rnd = new Random();
                int color = Color.rgb(rnd.nextInt(High-Low) + Low, rnd.nextInt(High-Low) + Low, rnd.nextInt(High-Low) + Low);
                for (Member member:members) {
                    drawableAvatar =member.getAvatarDrawable();
                    if ( drawableAvatar == null) {
                        return new LetterAvatar(getApplicationContext(),
                                color, member.getName().substring(0, 1).toUpperCase(), 24);

                    } else {
                        return drawableAvatar;
                    }
                }
                return ResourcesCompat.getDrawable(getResources(), Member.DEFAULT_AVATAR, null);
            }

            @Override
            protected void onPostExecute(Drawable drawableAvatar) {
                super.onPostExecute(drawableAvatar);
                float density = getApplicationContext().getResources().getDisplayMetrics().density;
                int sizeDp = (int)(48 * density);
                holder.mAvatarView.setImageBitmap(ImageUtils.getRoundedCornerBitmap(ImageUtils.convertToBitmap(drawableAvatar,sizeDp,sizeDp),48));
//                holder.mAvatarView.setImageDrawable(drawableAvatar);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mAvatarView;
            public final TextView mNameView;
            public final TextView mAddressView;
            public final LinearLayout mAccountNumbersView;
            public Member mMember;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mAvatarView = (ImageView) view.findViewById(R.id.avatar);
                mNameView = (TextView) view.findViewById(R.id.name);
                mAddressView = (TextView) view.findViewById(R.id.address);
                mAccountNumbersView = (LinearLayout) view.findViewById(R.id.llAccountNumbers);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'" + mAddressView.getText() + "'";
            }
        }
    }

    */
}