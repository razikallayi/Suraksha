package com.razikallayi.suraksha.member;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.utils.ImageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razi Kallayi on 06-12-2015.
 */
public class Member implements Serializable{
    public static final int DEFAULT_AVATAR = R.drawable.ic_default_avatar;

    private Long id;
    private String name;
    private String mobile;
    private String address;
    private String alias;
    private String father;
    private String spouse;
    private String gender;
    private Drawable avatar = null;
    private String age;
    private String occupation;
    private String remarks;
    private String nominee;
    private String relationWithNominee;
    private String addressOfNominee;
    private int isDeleted = 0;
    private String closedAt;
    private String createdAt;
    private String updatedAt;
    private List<Integer> accountNumbers = null;


    public Member(){

    }

    public Member(String name){
        this.name = name;
    }

    protected void incrementId(Context context) {
        //TODO use Max instead of count. otherwise it will be a problem when an entry is deleted
        Cursor cursor = context.getContentResolver().query(SurakshaContract.MemberEntry.CONTENT_URI, new String[]{SurakshaContract.MemberEntry._ID},null,null,null);
        if (cursor != null) {
            this.id = (long)(cursor.getCount() + 1);
            cursor.close();
        }
    }

    public Member(Context context, String name, String alias, String gender, String father, String spouse, String occupation, String age, String mobile, String address, String nominee, String relationWithNominee, String addressOfNominee, String remarks) {
        this.incrementId(context);
        this.name = name;
        this.alias = alias;
        this.gender = gender;
        this.father = father;
        this.spouse = spouse;
        this.occupation = occupation;
        this.age = age;
        this.mobile = mobile;
        this.remarks = remarks;
        this.address = address;
        this.nominee = nominee;
        this.relationWithNominee = relationWithNominee;
        this.addressOfNominee = addressOfNominee;
    }

    public static String[] MEMBER_COLUMNS = new String[] {
            SurakshaContract.MemberEntry.COLUMN_NAME,
            SurakshaContract.MemberEntry.COLUMN_ALIAS,
            SurakshaContract.MemberEntry.COLUMN_GENDER,
            SurakshaContract.MemberEntry.COLUMN_FATHER,
            SurakshaContract.MemberEntry.COLUMN_SPOUSE,
            SurakshaContract.MemberEntry.COLUMN_OCCUPATION,
            SurakshaContract.MemberEntry.COLUMN_AGE,
            SurakshaContract.MemberEntry.COLUMN_MOBILE,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS,
            SurakshaContract.MemberEntry.COLUMN_NOMINEE,
            SurakshaContract.MemberEntry.COLUMN_RELATION_WITH_NOMINEE,
            SurakshaContract.MemberEntry.COLUMN_ADDRESS_OF_NOMINEE,
            SurakshaContract.MemberEntry.COLUMN_REMARKS,
            SurakshaContract.MemberEntry.COLUMN_CLOSED_AT,
            SurakshaContract.MemberEntry.COLUMN_CREATED_AT,
            SurakshaContract.MemberEntry.COLUMN_UPDATED_AT,
            SurakshaContract.MemberEntry.COLUMN_AVATAR
    };
    // these indices must match the projection
    public static int COL_NAME                  =  0;
    public static int COL_ALIAS                 =  1;
    public static int COL_GENDER                =  2;
    public static int COL_FATHER                =  3;
    public static int COL_SPOUSE                =  4;
    public static int COL_OCCUPATION            =  5;
    public static int COL_AGE                   =  6;
    public static int COL_MOBILE                =  7;
    public static int COL_ADDRESS               =  8;
    public static int COL_NOMINEE               =  9;
    public static int COL_RELATION_WITH_NOMINEE =  10;
    public static int COL_ADDRESS_OF_NOMINEE    =  11;
    public static int COL_REMARKS               =  12;
    public static int COL_CLOSED_AT             =  13;
    public static int COL_CREATED_AT            =  14;
    public static int COL_UPDATED_AT            =  15;
    public static int COL_AVATAR                =  16;



    public static Member getMemberFromId(Context context,long id) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.MemberEntry.buildMemberUri(id), Member.MEMBER_COLUMNS,null,null,null);
        Member m = new Member();
        if (cursor != null) {
            cursor.moveToFirst();

            m.id = id;
            m.name = cursor.getString(COL_NAME);
            m.mobile = cursor.getString(COL_MOBILE);
            m.address = cursor.getString(COL_ADDRESS);
            m.alias = cursor.getString(COL_ALIAS);
            m.father = cursor.getString(COL_FATHER);
            m.spouse = cursor.getString(COL_SPOUSE);
            m.gender = cursor.getString(COL_GENDER);
            m.age = cursor.getString(COL_AGE);
            m.occupation = cursor.getString(COL_OCCUPATION);
            m.nominee = cursor.getString(COL_NOMINEE);
            m.relationWithNominee = cursor.getString(COL_RELATION_WITH_NOMINEE);
            m.addressOfNominee = cursor.getString(COL_ADDRESS_OF_NOMINEE);
            m.remarks = cursor.getString(COL_REMARKS);
            m.closedAt = cursor.getString(COL_CLOSED_AT);
            m.createdAt = cursor.getString(COL_CREATED_AT);
            m.updatedAt = cursor.getString(COL_UPDATED_AT);
            m.setAvatar(cursor.getBlob(COL_AVATAR));

            cursor.close();
        }
        return m;
    }


    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", gender='" + gender + '\'' +
                ", father='" + father + '\'' +
                ", spouse='" + spouse + '\'' +
                ", occupation='" + occupation + '\'' +
                ", age=" + age +
                ", mobile='" + mobile + '\'' +
                ", remarks='" + remarks + '\'' +
                ", address='" + address + '\'' +
                ", nominee='" + nominee + '\'' +
                ", relationWithNominee='" + relationWithNominee + '\'' +
                ", addressOfNominee='" + addressOfNominee + '\'' +
                ", isDeleted=" + isDeleted +
                ", closedAt=" + closedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static ContentValues getMemberContentValues(Member member){
        ContentValues values= new ContentValues();

        values.put(SurakshaContract.MemberEntry.COLUMN_NAME,                  member.getName()              );
        values.put(SurakshaContract.MemberEntry.COLUMN_ALIAS,                 member.getAlias()             );
        values.put(SurakshaContract.MemberEntry.COLUMN_GENDER,                member.getGender()            );
        values.put(SurakshaContract.MemberEntry.COLUMN_FATHER,                member.getFather()            );
        values.put(SurakshaContract.MemberEntry.COLUMN_SPOUSE,                member.getSpouse()            );
        values.put(SurakshaContract.MemberEntry.COLUMN_OCCUPATION,            member.getOccupation()        );
        values.put(SurakshaContract.MemberEntry.COLUMN_AVATAR,                member.getAvatar()            );
        values.put(SurakshaContract.MemberEntry.COLUMN_AGE,                   member.getAge()               );
        values.put(SurakshaContract.MemberEntry.COLUMN_MOBILE,                member.getMobile()            );
        values.put(SurakshaContract.MemberEntry.COLUMN_ADDRESS,               member.getAddress()           );
        values.put(SurakshaContract.MemberEntry.COLUMN_NOMINEE,               member.getNominee()           );
        values.put(SurakshaContract.MemberEntry.COLUMN_RELATION_WITH_NOMINEE, member.getRelationWithNominee());
        values.put(SurakshaContract.MemberEntry.COLUMN_ADDRESS_OF_NOMINEE,    member.getAddressOfNominee());
        values.put(SurakshaContract.MemberEntry.COLUMN_REMARKS,               member.getRemarks()           );
        values.put(SurakshaContract.MemberEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

        return values;
    }


    public List<Integer> fetchAccountNumbers(Context context) {
        List<Integer> acNumbers = new ArrayList<>();
        //Fetching accountNumbers
        Cursor cursorAccountNumbers = context.getContentResolver().query(SurakshaContract.AccountEntry
                        .buildAccountsOfMemberUri(this.id),
                new String[]{SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER}, null, null, null);
        if (cursorAccountNumbers != null) {
            while (cursorAccountNumbers.moveToNext()){
                acNumbers.add(cursorAccountNumbers.getInt(0));
            }
            cursorAccountNumbers.close();
        }
        this.accountNumbers = acNumbers;
        return acNumbers;
    }

    //Get count of all active members
    public static int getActiveMembersCount(Context context){
        Cursor cursor = context.getContentResolver().query(SurakshaContract.MemberEntry.CONTENT_URI, new String[]{SurakshaContract.MemberEntry._ID}, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }


    public List<Integer> getAccountNumbers() {
        return accountNumbers;
    }

    public byte[] getAvatar() {
        return ImageUtils.drawableToByteArray(avatar);
    }

    public Drawable getAvatarDrawable() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = ImageUtils.byteToDrawable(Resources.getSystem(),avatar);
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNominee() {
        return nominee;
    }

    public void setNominee(String nominee) {
        this.nominee = nominee;
    }

    public String getRelationWithNominee() {
        return relationWithNominee;
    }

    public void setRelationWithNominee(String relationWithNominee) {
        this.relationWithNominee = relationWithNominee;
    }

    public String getAddressOfNominee() {
        return addressOfNominee;
    }

    public void setAddressOfNominee(String addressOfNominee) {
        this.addressOfNominee = addressOfNominee;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
