package com.razikallayi.suraksha.member;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.RecyclerViewCursorAdapter;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.ImageUtils;
import com.razikallayi.suraksha.utils.LetterTileProvider;
import com.razikallayi.suraksha.utils.WordUtils;

/**
 * Created by Razi Kallayi on 10-04-2016.
 */

/**
 * {@link RecyclerView.Adapter} that can display a {@link Member} and makes a call to the
 * specified {@link OnItemClickListener}.
 */
public class MemberListAdapter extends RecyclerViewCursorAdapter<MemberListAdapter.MemberListViewHolder> {

    private static final String[] MEMBER_COLUMNS = {
            SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry._ID,
            SurakshaContract.MemberEntry.COLUMN_NAME,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS,
            SurakshaContract.MemberEntry.COLUMN_AVATAR,
            SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO,
            SurakshaContract.MemberEntry.COLUMN_CLOSED_AT,
    };
    private static final int COL_MEMBER_ID = 0;
    private static final int COL_MEMBER_NAME = 1;
    private static final int COL_MEMBER_ADDRESS = 2;
    private static final int COL_MEMBER_AVATAR = 3;
    private static final int COL_MEMBER_ACCOUNT_NO = 4;
    private static final int COL_CLOSED_AT = 5;
    //Refract onListFragmentInteractionListener on ItemCLickListner
    private OnItemClickListener mOnItemClickListener;

    private int selectedPos = RecyclerView.NO_POSITION;

    public MemberListAdapter() {
        super();
    }

    @Override
    public MemberListViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_list_content, parent, false);
//        view.setOnClickListener(this);

        return new MemberListViewHolder(view);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final MemberListViewHolder holder, final Cursor cursor, int position) {
        holder.mView.setSelected(selectedPos == position);
        // Set selected state; use a state list drawable to style the view
        holder.bindData(cursor);
    }

    private Bitmap getAvatarBitmap(Context context, Member member) {
        float density = context.getResources().getDisplayMetrics().density;
        int avatarPixel = 52;
        int sizeDp = (int) (avatarPixel * density);
        int cornerRadius = sizeDp / 2;
        Drawable drawableAvatar = null;

        drawableAvatar = member.getAvatarDrawable();
        String key = String.valueOf(member.getAccountNo());
        String name = WordUtils.toTitleCase(member.getName());
        if (drawableAvatar == null) {
            return ImageUtils.getRoundedCornerBitmap(new LetterTileProvider(context)
                    .getLetterTile(name, key, sizeDp), cornerRadius);

        } else {
            return ImageUtils.getRoundedCornerBitmap(ImageUtils.convertToBitmap(drawableAvatar,
                    sizeDp, sizeDp), cornerRadius);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, long memberId, String memberName);
    }

    public class MemberListViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mAddressView;
        private TextView mAccountNumberView;
        private TextView mStatus;
        //        private LinearLayout mAccountNumbersView;
        private Member mMember;

        public MemberListViewHolder(final View view) {
            super(view);
            mView = view;
            mAvatarView = view.findViewById(R.id.avatar);
            mNameView = view.findViewById(R.id.name);
            mAddressView = view.findViewById(R.id.address);
            mAccountNumberView = view.findViewById(R.id.lblAccountNumber);
            mStatus = view.findViewById(R.id.status);
//            mAccountNumbersView = (LinearLayout) view.findViewById(R.id.llAccountNumbers);
        }

        public void bindData(final Cursor cursor) {

            Member member = new Member();
            member.setId(cursor.getLong(COL_MEMBER_ID));
            member.setName(cursor.getString(COL_MEMBER_NAME));
            member.setAddress(cursor.getString(COL_MEMBER_ADDRESS));
            member.setAccountNo(cursor.getInt(COL_MEMBER_ACCOUNT_NO));
            member.setAvatar(cursor.getBlob(COL_MEMBER_AVATAR));
            member.setClosedAt(cursor.getLong(COL_CLOSED_AT));

            mMember = member;

            mNameView.setText(member.getName());
            mAddressView.setText(member.getAddress());
            mAccountNumberView.setText(String.valueOf(member.getAccountNo()));
            mAvatarView.setImageBitmap(getAvatarBitmap(mAvatarView.getContext(), member));
            mStatus.setVisibility(View.GONE);
            if (member.isAccountClosed()) {
                mStatus.setText("Closed");
                mStatus.setVisibility(View.VISIBLE);
            }


            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mOnItemClickListener.onItemClick(mView, mMember.getId(),
                                mMember.getName());
                        notifyItemChanged(selectedPos);
                        selectedPos = getLayoutPosition();
                        notifyItemChanged(selectedPos);
                    }
                }
            });
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'" + mAddressView.getText() + "'";
        }
    }
}