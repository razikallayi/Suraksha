package com.razikallayi.suraksha_ssf;

import android.test.AndroidTestCase;

/**
 * Created by Razi Kallayi on 10-01-2016.
 */
public class TestDb extends AndroidTestCase {
  /*  public static final String LOG_TAG = TestDb.class.getSimpleName();
    public void testCreateDb() {
        mContext.deleteDatabase(SurakshaDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new SurakshaDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }
    static public ContentValues getMemberContentValues(){
        Member testMember = new Member("Tester");

        ContentValues values= new ContentValues();
        values.put(SurakshaContract.MemberEntry.COLUMN_NAME ,testMember.getName());
        return values;
    }

    public void testInsertReadDb(){


        SurakshaDbHelper dbHelper = new SurakshaDbHelper(mContext);
        SQLiteDatabase db  = dbHelper.getWritableDatabase();

        ContentValues values = getMemberContentValues();

        long memberRowId;
        memberRowId = db.insert(SurakshaContract.MemberEntry.TABLE_NAME,null,values);

        assertTrue(memberRowId != -1);
        Log.d(LOG_TAG, "New row id: " + memberRowId);

        Cursor cursor = db.query(SurakshaContract.MemberEntry.TABLE_NAME,
                null,
                null, //Columns for the "where" clause
                null, //Values for the "where" clause
                null, //Columns to group by
                null, //columns to filter by row group
                null  //sort order
        );

        if (cursor.moveToFirst()) {
            validateCursor(values,cursor);
        }
        else{
            fail("No values returned");
        }
    }
    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor){
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String,Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == index);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(index));
        }
    }*/
}
