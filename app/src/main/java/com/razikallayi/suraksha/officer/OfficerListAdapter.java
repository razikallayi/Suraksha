package com.razikallayi.suraksha.officer;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.data.SurakshaContract;

public class OfficerListAdapter extends RecyclerViewCursorAdapter<OfficerListAdapter.ViewHolder> {

    private final OfficerListFragment.OnClickOfficerListItemListener mListener;

    public OfficerListAdapter(OfficerListFragment.OnClickOfficerListItemListener listener) {
        super();
        mListener = listener;
    }

    private static final String[] OFFICER_COLUMNS = {
            SurakshaContract.OfficerEntry.TABLE_NAME+"."+SurakshaContract.MemberEntry._ID,
            SurakshaContract.OfficerEntry.COLUMN_NAME,
    };

    private static final int COL_OFFICER_ID = 0;
    private static final int COL_OFFICER_NAME = 1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.officer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        // The Cursor is now set to the right position
        Officer officer = new Officer();
        officer.setId(cursor.getLong(COL_OFFICER_ID));
        officer.setName(cursor.getString(COL_OFFICER_NAME));
        holder.mOfficer = officer;
        holder.mOfficerName.setText(officer.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onClickOfficerListItem(holder.mOfficer);
                }
            }
        });
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mOfficerName;
        public Officer mOfficer;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOfficerName = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mOfficerName.getText() + "'";
        }
    }
}
