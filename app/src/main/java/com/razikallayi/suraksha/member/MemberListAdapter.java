package com.razikallayi.suraksha.member;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.ImageUtils;
import com.razikallayi.suraksha.utils.LetterAvatar;

import java.util.List;
import java.util.Random;

/**
 * Created by Razi Kallayi on 10-04-2016.
 */

/**
 * {@link RecyclerView.Adapter} that can display a {@link Member} and makes a call to the
 * specified {@link OnItemClickListener}.
 */
public class MemberListAdapter extends RecyclerViewCursorAdapter<MemberListAdapter.MemberListViewHolder>
        implements View.OnClickListener {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_NORMAL_MEMBER = 0;
    private static final int VIEW_TYPE_RED_MARK_MEMBER = 1;


    //Refract onListFragmentInteractionListener on ItemCLickListner
    private OnItemClickListener mOnItemClickListener;
    // Start with first item selected
    private int focusedItem = -1;

    private static final String[] MEMBER_COLUMNS = {
            SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry._ID,
            SurakshaContract.MemberEntry.COLUMN_NAME,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS,
            SurakshaContract.MemberEntry.COLUMN_AVATAR,
    };

    private static final int COL_MEMBER_ID = 0;
    private static final int COL_MEMBER_NAME = 1;
    private static final int COL_MEMBER_ADDRESS = 2;
    private static final int COL_MEMBER_AVATAR = 3;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mRedMemberLayout = true;

    public MemberListAdapter() {
        super();
    }

    @Override
    public MemberListViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_list_content, parent, false);
        view.setOnClickListener(this);
        return new MemberListViewHolder(view);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    /*
     * View.OnClickListener
     */
    @Override
    public void onClick(View view) {
        if (null != mOnItemClickListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            RecyclerView recyclerView = (RecyclerView) view.getParent();
            int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                Cursor cursor = this.getItem(position);
                this.mOnItemClickListener.onItemClick(cursor.getLong(COL_MEMBER_ID),
                        cursor.getString(COL_MEMBER_NAME));
            }
        }
    }

    @Override
    public void onBindViewHolder(final MemberListViewHolder holder, final Cursor cursor) {
        // Set selected state; use a state list drawable to style the view
        holder.bindData(cursor);
    }


    public class MemberListViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mAddressView;
        private LinearLayout mAccountNumbersView;
        private Member mMember;

        public MemberListViewHolder(final View view) {
            super(view);
            mView = view;
            mAvatarView = (ImageView) view.findViewById(R.id.avatar);
            mNameView = (TextView) view.findViewById(R.id.name);
            mAddressView = (TextView) view.findViewById(R.id.address);
            mAccountNumbersView = (LinearLayout) view.findViewById(R.id.llAccountNumbers);
        }

        public void bindData(Cursor cursor) {
            Member member = new Member();
            // The Cursor is now set to the right position
            member.setId(cursor.getLong(COL_MEMBER_ID));
            member.setName(cursor.getString(COL_MEMBER_NAME));
            member.setAddress(cursor.getString(COL_MEMBER_ADDRESS));
            member.setAvatar(cursor.getBlob(COL_MEMBER_AVATAR));
            member.fetchAccountNumbers(mView.getContext());
            mMember = member;

            List<Integer> accountNumbers = member.getAccountNumbers();
            //Remove all views, else account numbers will get repopulated in a random order
            mAccountNumbersView.removeAllViews();
            for (int accountNumber : accountNumbers) {
                TextView tvAccountNumber = new TextView(mView.getContext());
                tvAccountNumber.setText(String.valueOf(accountNumber));
                tvAccountNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mView.getContext().getResources().getDimension(R.dimen.font_small));
                tvAccountNumber.setTypeface(null, Typeface.BOLD);

                tvAccountNumber.setTextColor(ContextCompat.getColor(mView.getContext(), R.color.white));
                tvAccountNumber.setBackground(ResourcesCompat.getDrawable(mView.getContext().getResources(), R.drawable.ic_circle, null));
                tvAccountNumber.setGravity(Gravity.CENTER);
                //set Padding
                int sidePaddingPixel = 2;
                int sidePaddingDp = (int) (sidePaddingPixel * mView.getContext().getResources().getDisplayMetrics().density);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(sidePaddingDp, sidePaddingDp, sidePaddingDp, sidePaddingDp);
                mAccountNumbersView.addView(tvAccountNumber, params);
            }

            SetAvatarTask t = new SetAvatarTask(mView.getContext(), MemberListViewHolder.this);
            t.execute(member);

            mNameView.setText(member.getName());
            mAddressView.setText(member.getAddress());


            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mOnItemClickListener.onItemClick(mMember.getId(), mMember.getName());
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'" + mAddressView.getText() + "'";
        }
    }


    private class SetAvatarTask extends AsyncTask<Member, Void, Bitmap> {
        private Context mContext;
        private MemberListViewHolder holder;

        public SetAvatarTask(Context context, MemberListViewHolder viewHolder) {
            mContext = context;
            holder = viewHolder;

        }

        @Override
        protected Bitmap doInBackground(Member... members) {
            float density = mContext.getResources().getDisplayMetrics().density;
            int avatarPixel = 56;
            int sizeDp = (int) (avatarPixel * density);
            Drawable drawableAvatar = null;
            final int low = 50;
            final int high = 200;
            Random rnd = new Random();
            int color = Color.rgb(rnd.nextInt(high - low) + low, rnd.nextInt(high - low) + low, rnd.nextInt(high - low) + low);
            String firstLetter = members[0].getName().substring(0, 1).toUpperCase();
            drawableAvatar = members[0].getAvatarDrawable();
            if (drawableAvatar == null) {
                return ImageUtils.getRoundedCornerBitmap(ImageUtils.convertToBitmap(new LetterAvatar(mContext,
                        color, firstLetter, 32), sizeDp, sizeDp), avatarPixel);

            } else {
                return ImageUtils.getRoundedCornerBitmap(ImageUtils.convertToBitmap(drawableAvatar, sizeDp, sizeDp), avatarPixel);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmapAvatar) {
            super.onPostExecute(bitmapAvatar);
            holder.mAvatarView.setImageBitmap(bitmapAvatar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(long memberId, String memberName);
    }
}