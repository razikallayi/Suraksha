package com.razikallayi.suraksha_ssf.officer;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razikallayi.suraksha_ssf.R;
import com.razikallayi.suraksha_ssf.data.SurakshaContract;
import com.razikallayi.suraksha_ssf.utils.AuthUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class OfficerListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = OfficerListFragment.class.getClass().getSimpleName();

    private int mColumnCount = 1;

    private OnClickOfficerListItemListener mListener;

    private static final int OFFICER_LIST_LOADER = 0;
    private OfficerListAdapter mOfficerListAdapter;

    private static final String[] OFFICER_COLUMNS = {
            SurakshaContract.OfficerEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry._ID,
            SurakshaContract.OfficerEntry.COLUMN_NAME,
            SurakshaContract.OfficerEntry.COLUMN_IS_ADMIN
    };

    private static final int COL_OFFICER_ID = 0;
    private static final int COL_OFFICER_NAME = 1;
    private static final int COL_OFFICER_IS_ADMIN = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfficerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOfficerListAdapter = new OfficerListAdapter(mListener);
        getLoaderManager().initLoader(OFFICER_LIST_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.officer_list_recycler_view, container, false);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mOfficerListAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClickOfficerListItemListener) {
            mListener = (OnClickOfficerListItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClickOfficerListItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        boolean isAdmin = AuthUtils.isAdmin(getContext());
        String authOfficerId = String.valueOf(AuthUtils.getAuthenticatedOfficerId(getContext()));
        if (isAdmin) {
            return new CursorLoader(getContext(),
                    SurakshaContract.OfficerEntry.CONTENT_URI, OFFICER_COLUMNS, null, null, null);
        } else {
            return new CursorLoader(getContext(),
                    SurakshaContract.OfficerEntry.CONTENT_URI, OFFICER_COLUMNS,
                    SurakshaContract.OfficerEntry._ID + " = ? ",
                    new String[]{authOfficerId}, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mOfficerListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOfficerListAdapter.swapCursor(null);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnClickOfficerListItemListener {
        void onClickOfficerListItem(Officer officer);
    }
}