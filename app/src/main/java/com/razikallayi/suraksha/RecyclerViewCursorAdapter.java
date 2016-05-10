package com.razikallayi.suraksha;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Razi Kallayi on 10-04-2016.
 */
public abstract class RecyclerViewCursorAdapter<ViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolder>{

    private Cursor cursor;

    public void swapCursor(final Cursor cursor)
    {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return this.cursor != null
                ? this.cursor.getCount()
                : 0;
    }

    public Cursor getItem(final int position)
    {
        if (this.cursor != null && !this.cursor.isClosed())
        {
            this.cursor.moveToPosition(position);
        }

        return this.cursor;
    }

    public Cursor getCursor()
    {
        return this.cursor;
    }

    @Override
    public final void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final Cursor cursor = this.getItem(position);
        this.onBindViewHolder(holder, cursor);
    }
    public abstract void onBindViewHolder(final ViewHolder holder, final Cursor cursor);
}
