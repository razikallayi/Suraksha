package com.razikallayi.suraksha.member;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.razikallayi.suraksha.AvatarActivity;
import com.razikallayi.suraksha.R;
import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.loan.LoanIssue;
import com.razikallayi.suraksha.txn.Transaction;
import com.razikallayi.suraksha.utils.AuthUtils;
import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.utils.ImageUtils;
import com.razikallayi.suraksha.utils.LetterTileProvider;
import com.razikallayi.suraksha.utils.Utility;
import com.razikallayi.suraksha.utils.WordUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Razi Kallayi on 06-12-2015.
 */
public class Member implements Serializable {
    public static final int DEFAULT_AVATAR = R.drawable.ic_default_avatar;

    private Long id;
    private String name;
    private int accountNo;
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
    private boolean hasActiveLoan = false;
    private boolean isLoanBlocked = false;
    private boolean isSmsEnabled = true;
    private boolean isDeleted = false;
    private long closedAt;
    private long createdAt;
    private long updatedAt;
//    private List<Integer> accountNumbers = null;


    public Member() {

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

    /**
     * Return the next account number to be inserted to database.
     *
     * @param context Context used to getContentResolver
     * @return int
     */
    public static int generateAccountNumber(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.MemberEntry.CONTENT_URI,
                new String[]{"Max(" + SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + ")"}, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int accountNumber = cursor.getInt(0) + 1;
            cursor.close();
            return accountNumber;
        }
        return 0;
    }

    public static Member getMemberFromId(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.MemberEntry.buildMemberUri(id),
                Member.MemberQuery.PROJECTION, null, null, null);

        return getMemberFromCursor(cursor);
    }

    public static Member getMemberFromAccountNumber(Context context, int accountNumber) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.MemberEntry.CONTENT_URI, MemberQuery.PROJECTION,
                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + " = ? ",
                new String[]{String.valueOf(accountNumber)},
                null);
        return getMemberFromCursor(cursor);
    }

//    public static Member getBasicMemberFromAccountNumber(Context context, int accountNumber) {
//        Cursor cursor = context.getContentResolver().query(
//                SurakshaContract.MemberEntry.CONTENT_URI, MemberQuery.PROJECTION,
//                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + " = ? ",
//                new String[]{String.valueOf(accountNumber)},
//                null);
//        return getMemberFromCursor(cursor);
//    }

    public static Member getMemberFromCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            Member m = new Member();
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
            }

            m.id = cursor.getLong(MemberQuery.COL_ID);
            m.name = cursor.getString(MemberQuery.COL_NAME);
            m.accountNo = cursor.getInt(MemberQuery.COL_ACCOUNT_NO);
            m.mobile = cursor.getString(MemberQuery.COL_MOBILE);
            m.address = cursor.getString(MemberQuery.COL_ADDRESS);
            m.alias = cursor.getString(MemberQuery.COL_ALIAS);
            m.father = cursor.getString(MemberQuery.COL_FATHER);
            m.spouse = cursor.getString(MemberQuery.COL_SPOUSE);
            m.gender = cursor.getString(MemberQuery.COL_GENDER);
            m.age = cursor.getString(MemberQuery.COL_AGE);
            m.occupation = cursor.getString(MemberQuery.COL_OCCUPATION);
            m.nominee = cursor.getString(MemberQuery.COL_NOMINEE);
            m.relationWithNominee = cursor.getString(MemberQuery.COL_RELATION_WITH_NOMINEE);
            m.addressOfNominee = cursor.getString(MemberQuery.COL_ADDRESS_OF_NOMINEE);
            m.remarks = cursor.getString(MemberQuery.COL_REMARKS);
            m.isLoanBlocked = cursor.getInt(MemberQuery.COL_IS_LOAN_BLOCKED) > 0;
            m.isSmsEnabled = cursor.getInt(MemberQuery.COL_IS_SMS_ENABLED) > 0;
            m.hasActiveLoan = cursor.getInt(MemberQuery.COL_HAS_LOAN) > 0;
            m.closedAt = cursor.getLong(MemberQuery.COL_CLOSED_AT);
            m.createdAt = cursor.getLong(MemberQuery.COL_CREATED_AT);
            m.updatedAt = cursor.getLong(MemberQuery.COL_UPDATED_AT);
            m.setAvatar(cursor.getBlob(MemberQuery.COL_AVATAR));
            cursor.close();
            return m;
        }
        return null;
    }

    public static ContentValues getMemberContentValues(Member member) {
        ContentValues values = new ContentValues();

        values.put(SurakshaContract.MemberEntry.COLUMN_NAME, member.name);
        values.put(SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO, member.accountNo);
        values.put(SurakshaContract.MemberEntry.COLUMN_ALIAS, member.alias);
        values.put(SurakshaContract.MemberEntry.COLUMN_GENDER, member.gender);
        values.put(SurakshaContract.MemberEntry.COLUMN_FATHER, member.father);
        values.put(SurakshaContract.MemberEntry.COLUMN_SPOUSE, member.spouse);
        values.put(SurakshaContract.MemberEntry.COLUMN_OCCUPATION, member.occupation);
        values.put(SurakshaContract.MemberEntry.COLUMN_AVATAR, member.getAvatar());
        values.put(SurakshaContract.MemberEntry.COLUMN_AGE, member.age);
        values.put(SurakshaContract.MemberEntry.COLUMN_MOBILE, member.mobile);
        values.put(SurakshaContract.MemberEntry.COLUMN_ADDRESS, member.address);
        values.put(SurakshaContract.MemberEntry.COLUMN_NOMINEE, member.nominee);
        values.put(SurakshaContract.MemberEntry.COLUMN_RELATION_WITH_NOMINEE, member.relationWithNominee);
        values.put(SurakshaContract.MemberEntry.COLUMN_ADDRESS_OF_NOMINEE, member.addressOfNominee);
        values.put(SurakshaContract.MemberEntry.COLUMN_REMARKS, member.remarks);
        values.put(SurakshaContract.MemberEntry.COLUMN_HAS_LOAN, member.hasActiveLoan);
        values.put(SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED, member.isLoanBlocked);
        values.put(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT, member.closedAt);
        values.put(SurakshaContract.MemberEntry.COLUMN_IS_DELETED, member.isDeleted);
        if (member.createdAt == 0) {
            values.put(SurakshaContract.MemberEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        }
        values.put(SurakshaContract.MemberEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());

        return values;
    }

//    public static List<Transaction> fetchDeposits(Context context, String accountNumber) {
//        Cursor cursor = context.getContentResolver().query(
//                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
//                Transaction.TxnQuery.PROJECTION,
//                SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
//                        + SurakshaContract.TxnEntry.COLUMN_ACCOUNT_NUMBER + "= ?",
//                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER), accountNumber},
//                SurakshaContract.TxnEntry.COLUMN_CREATED_AT + " DESC");
//        return Transaction.getTxnListFromCursor(context, cursor);
//    }

    @Deprecated
    //Get count of all active members
    public static int getActiveMembersCount(Context context) {
        // TODO: 19-06-2016 Check for Active members instead of all members. Use selection (where)
        Cursor cursor = context.getContentResolver().query(SurakshaContract.MemberEntry.CONTENT_URI,
                new String[]{SurakshaContract.MemberEntry._ID}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    public LoanIssue getActiveLoan(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.LoanIssueEntry.CONTENT_URI, LoanIssue.LoanIssueQuery.PROJECTION,
                SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER + " = ? and "
                        + SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT + " = 0 ",
                new String[]{String.valueOf(accountNo)},
                null);
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        return LoanIssue.getLoanIssueFromCursor(context, cursor);
    }

    public LoanIssue getActiveBystanderLoan(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.LoanIssueEntry.CONTENT_URI,
                LoanIssue.LoanIssueQuery.PROJECTION,
                SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER + " = ? and "
                        + SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT + " = 0 ",
                new String[]{String.valueOf(accountNo)},
                null);
        if (cursor == null) return null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return LoanIssue.getLoanIssueFromCursor(context, cursor);
        }
        return null;
    }

    private void incrementId(Context context) {
        Cursor cursor = context.getContentResolver().query(SurakshaContract.MemberEntry.CONTENT_URI,
                new String[]{"Max(" + SurakshaContract.MemberEntry._ID + ")"}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            this.id = (long) (cursor.getInt(0) + 1);
            cursor.close();
        }
    }

    public boolean isAccountClosed() {
        return closedAt > 0;
    }


    public Transaction getCloseAccountTxn(Context context) {
        String selectionTxn = SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ? ";

        String[] selectionArgTxn = new String[]{
                String.valueOf(SurakshaContract.TxnEntry.ACCOUNT_CLOSE_LEDGER),
                String.valueOf(accountNo)};
        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.CONTENT_URI,
                Transaction.TxnQuery.PROJECTION, selectionTxn,
                selectionArgTxn,
                null);
        if (cursor == null) return null;
        if (cursor.getCount() == 1) {
            return Transaction.getTxnFromCursor(context, cursor);
        }
        if (cursor.getCount() > 1) {
            //If more than one closing entry, remove all others and return first txn.
            List<Transaction> txns = Transaction.getTxnListFromCursor(context, cursor);
            int i;
            for (i = 1; i <= txns.size(); i++) {
                txns.get(i).destroy(context);
            }
            return txns.get(0);
        }
        return null;
    }


    public Transaction makeDeposit(Context context, long depositMonth, long paymentDate, String remarks) {
        Transaction txnMonthlyDeposit = new Transaction(context, accountNo, Utility.getMonthlyDepositAmount(),
                SurakshaContract.TxnEntry.RECEIPT_VOUCHER, SurakshaContract.TxnEntry.DEPOSIT_LEDGER,
                remarks, AuthUtils.getAuthenticatedOfficerId(context));
        txnMonthlyDeposit.setDepositForDate(depositMonth);
        txnMonthlyDeposit.setPaymentDate(paymentDate);
        txnMonthlyDeposit.setCreatedAt(System.currentTimeMillis());
        ContentValues values = Transaction.getTxnContentValues(txnMonthlyDeposit);
        String selectionTxn = SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ? AND "
                + SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE + "= ? AND "
                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + "=?";

        String[] selectionArgTxn = new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),
                String.valueOf(accountNo), String.valueOf(depositMonth),
                String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)};

        Cursor cursor = context.getContentResolver().query(SurakshaContract.TxnEntry.CONTENT_URI,
                new String[]{SurakshaContract.TxnEntry._ID}, selectionTxn,
                selectionArgTxn,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return null;
        }
        context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);


        return txnMonthlyDeposit;
    }

    public Transaction updateDeposit(Context context, Transaction depositTxn, long paymentDate, String remarks) {
        depositTxn.setPaymentDate(paymentDate);
        depositTxn.setNarration(remarks);
        depositTxn.setUpdatedAt(System.currentTimeMillis());
        ContentValues values = Transaction.getTxnContentValues(depositTxn);
        context.getContentResolver().update(SurakshaContract.TxnEntry.CONTENT_URI,
                values,
                SurakshaContract.TxnEntry._ID + "=?",
                new String[]{String.valueOf(depositTxn.getId())});
        return depositTxn;
    }


    public boolean saveIsSmsEnabled(Context context, boolean isSmsEnabled) {
        ContentValues values = new ContentValues();
        values.put(SurakshaContract.MemberEntry.COLUMN_IS_SMS_ENABLED, isSmsEnabled ? 1 : 0);
        int numRowsUpdated = context.getContentResolver().update(
                SurakshaContract.MemberEntry.CONTENT_URI,
                values,
                SurakshaContract.MemberEntry._ID + " = ? ",
                new String[]{String.valueOf(this.id)});
        setSmsEnabled(isSmsEnabled);
        return numRowsUpdated > 0;
    }

    public boolean saveIsLoanBlocked(Context context, boolean isLoanBlocked) {
        ContentValues values = new ContentValues();
        values.put(SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED, isLoanBlocked ? 1 : 0);
        int numRowsUpdated = context.getContentResolver().update(
                SurakshaContract.MemberEntry.CONTENT_URI,
                values,
                SurakshaContract.MemberEntry._ID + " = ? ",
                new String[]{String.valueOf(this.id)});
        setLoanBlocked(isLoanBlocked);
        return numRowsUpdated > 0;
    }

    public boolean saveHasLoan(Context context, boolean hasLoan) {
        ContentValues values = new ContentValues();
        values.put(SurakshaContract.MemberEntry.COLUMN_HAS_LOAN, hasLoan ? 1 : 0);
        int numRowsUpdated = context.getContentResolver().update(
                SurakshaContract.MemberEntry.CONTENT_URI,
                values,
                SurakshaContract.MemberEntry._ID + " = ? ",
                new String[]{String.valueOf(this.id)});
        setHasActiveLoan(hasLoan);
        return numRowsUpdated > 0;
    }

    public int closeAccount(Context context, double closingBalance, long closedDate, String remarks) {
        if (isAccountClosed()) return 0;
        if (getActiveBystanderLoan(context) != null) return 0;
        if (getCloseAccountTxn(context) != null) return 0;

        LoanIssue activeLoan = getActiveLoan(context);
        if (activeLoan != null) {
            int lastInstalment = activeLoan.lastInstalmentNumber(context);
            if (!activeLoan.isByStanderReleased(lastInstalment)) {
                activeLoan.getSecurityMember(context).saveIsLoanBlocked(context, false);
            }
            activeLoan.closeLoan(context);
            saveHasLoan(context, false);
            saveIsLoanBlocked(context, true);
        }

        int voucherType;
        if (closingBalance > 0) {
            voucherType = SurakshaContract.TxnEntry.PAYMENT_VOUCHER;
        } else {
            voucherType = SurakshaContract.TxnEntry.RECEIPT_VOUCHER;
        }

        Transaction closeAccountTxn = new Transaction(context, accountNo, Math.abs(closingBalance),
                voucherType, SurakshaContract.TxnEntry.ACCOUNT_CLOSE_LEDGER,
                remarks, AuthUtils.getAuthenticatedOfficerId(context));
        closeAccountTxn.setPaymentDate(closedDate);
        closeAccountTxn.setCreatedAt(System.currentTimeMillis());
        ContentValues values = Transaction.getTxnContentValues(closeAccountTxn);
        context.getContentResolver().insert(SurakshaContract.TxnEntry.CONTENT_URI, values);

        ContentValues memberValues = new ContentValues();
        memberValues.put(SurakshaContract.MemberEntry.COLUMN_CLOSED_AT, closedDate);
        int numRowsUpdated = context.getContentResolver().update(
                SurakshaContract.MemberEntry.CONTENT_URI,
                memberValues,
                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO + " = ? ",
                new String[]{String.valueOf(this.accountNo)});
        return numRowsUpdated;
    }

    /*
     *
     * -if member not has loan return false
     * -check current date.
     * -if it is notDueDate return false
     * -check loan issued date
     * -check last loan return date
     * -if current date is 15 of next month  of loan issued date or loanReturn date, the loan is due
     * */
    public boolean hasLoanDue(Context context) {
        if (!hasActiveLoan) {
            return false;
        } else {
//            LoanIssued date
            LoanIssue loanIssued = getActiveLoan(context);
            boolean hasInstalmentDue = loanIssued.hasInstalmentDue(context);
            return hasInstalmentDue;
//            Calendar nextInstalmentCalendar = getNextInstalmentCalendar(context, loanIssued);
//            return nextInstalmentCalendar != null &&
//                    CalendarUtils.getDepositStartDay() >= nextInstalmentCalendar.getTimeInMillis();
        }
    }
//
//    public Calendar getNextInstalmentCalendar(Context context, LoanIssue loanIssued) {
//        if (hasActiveLoan) {
//            Calendar loanIssuedDateCalendar = Calendar.getInstance();
//            loanIssuedDateCalendar.setTimeInMillis(loanIssued.getIssuedAt());
//            loanIssuedDateCalendar.add(Calendar.MONTH, loanIssued.nextInstalmentNumber(context));
//            loanIssuedDateCalendar.set(Calendar.DATE, CalendarUtils.getDueDay());
//            return CalendarUtils.normalizeDate(loanIssuedDateCalendar);
//        }
//        return null;
//    }

    public Calendar getNextDepositMonthCalendar(List<Transaction> depositedTxnList) {
        Calendar depositMonth = CalendarUtils.getInstance();
        if (depositedTxnList.isEmpty()) {
            depositMonth = CalendarUtils.getSurakshaStartDate();
        } else {
            Transaction month = depositedTxnList.get(0);
            depositMonth.setTimeInMillis(month.getDefinedDepositMonth());
            depositMonth.add(Calendar.MONTH, 1);
        }
        return depositMonth;
    }

    public Calendar getNextDepositMonthCalendar(Context context) {
        List<Transaction> depositedTxnList = fetchDeposits(context);
        return getNextDepositMonthCalendar(depositedTxnList);
    }

    public boolean hasDepositDue(Context context) {
        /*if (!CalendarUtils.isDepositStartDay()) {
            return false;
        }*/
        Calendar nextDepositMonth = getNextDepositMonthCalendar(context);
        nextDepositMonth.set(Calendar.DATE, CalendarUtils.getDueDay());
        return CalendarUtils.getDepositStartDay() >= CalendarUtils.normalizeDate(nextDepositMonth.getTimeInMillis());
    }

    public List<Transaction> fetchDeposits(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
                Transaction.TxnQuery.PROJECTION,
                SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ?",
                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),
                        String.valueOf(accountNo)},
                SurakshaContract.TxnEntry.COLUMN_CREATED_AT + " DESC");
        return Transaction.getTxnListFromCursor(context, cursor);
    }
/*
    public List<LoanIssue> getLoansIssued(Context context) {
        String selection = SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                + SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_LEDGER + " = ? and "
                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(accountNo),
                String.valueOf(SurakshaContract.TxnEntry.LOAN_ISSUED_LEDGER),
                String.valueOf(SurakshaContract.TxnEntry.PAYMENT_VOUCHER)};
        String sortOrder = SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                + SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT + " desc";

        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.LoanIssueEntry.CONTENT_URI,
                LoanIssue.LoanIssueQuery.PROJECTION
                , selection, selectionArgs, sortOrder);
        return LoanIssue.getLoanIssueListFromCursor(context, cursor);
    }*/

    public int noOfLoansIssued(Context context) {
        String[] projection = new String[]{"count(" + SurakshaContract.LoanIssueEntry.TABLE_NAME + "." + SurakshaContract.LoanIssueEntry._ID + ")"};
        String selection = SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                + SurakshaContract.LoanIssueEntry.COLUMN_ACCOUNT_NUMBER + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(accountNo)};
        String sortOrder = SurakshaContract.LoanIssueEntry.TABLE_NAME + "."
                + SurakshaContract.LoanIssueEntry.COLUMN_ISSUED_AT + " desc";

        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.LoanIssueEntry.LOANISSUE_ONLY_CONTENT_URI,
                projection
                , selection, selectionArgs, sortOrder);
        int count = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }


    public Transaction getLastDepositMonthTxn(Context context) {
        Transaction txn = null;

        String selectionMax = SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ? AND "
                + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + "=?";
        String[] selectionArgMax = new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),
                String.valueOf(accountNo),
                String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)};

        Cursor cursorMax = context.getContentResolver().query(
                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
                new String[]{"Max(" + SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE + ")"},
                selectionMax,
                selectionArgMax, null);
        if (cursorMax != null) {
            cursorMax.moveToFirst();
            long maxDate = cursorMax.getLong(0);

            String selectionTxn = SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                    + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ? AND "
                    + SurakshaContract.TxnEntry.COLUMN_DEPOSIT_FOR_DATE + "= ? AND "
                    + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + "=?";

            String[] selectionArgTxn = new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),
                    String.valueOf(accountNo), String.valueOf(maxDate),
                    String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER)};

            Cursor cursor = context.getContentResolver().query(
                    SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
                    Transaction.TxnQuery.PROJECTION, selectionTxn, selectionArgTxn, null);
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                txn = Transaction.getTxnFromCursor(context, cursor);
                cursor.close();
            }
            cursorMax.close();
        }
        return txn;
    }


    public int getNumberOfDeposits(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.buildFetchAllDepositsUri(),
                new String[]{
                        SurakshaContract.TxnEntry.TABLE_NAME + "." + SurakshaContract.TxnEntry._ID},
                SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ?",
                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),
                        String.valueOf(accountNo)}, null);

        if (cursor != null && cursor.getCount() > 0) {
            int count = cursor.getCount();
            cursor.close();
            return count;
        } else {
            return 0;
        }
    }

    public double sumOfDeposits(Context context) {
        Cursor cursor = context.getContentResolver().query(
                SurakshaContract.TxnEntry.CONTENT_URI,
                new String[]{"sum(" + SurakshaContract.TxnEntry.COLUMN_AMOUNT + ")"},
                SurakshaContract.TxnEntry.COLUMN_LEDGER + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_VOUCHER_TYPE + "= ? AND "
                        + SurakshaContract.TxnEntry.COLUMN_FK_ACCOUNT_NUMBER + "= ?",
                new String[]{String.valueOf(SurakshaContract.TxnEntry.DEPOSIT_LEDGER),
                        String.valueOf(SurakshaContract.TxnEntry.RECEIPT_VOUCHER),
                        String.valueOf(accountNo)}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            double sum = cursor.getDouble(0);
            cursor.close();
            return sum;
        } else {
            return 0.0;
        }
    }

    public String getInfo() {
        return name + " " + address + " [ " + accountNo + " ] ";
    }

//    @Deprecated
//    public List<Integer> fetchAccountNumbers(Context context) {
//        List<Integer> acNumbers = new ArrayList<>();
//        //Fetching accountNumbers
//        Cursor cursorAccountNumbers = context.getContentResolver().query(
//                SurakshaContract.AccountEntry.buildAccountsOfMemberUri(this.id), new String[]{
//                        SurakshaContract.AccountEntry.TABLE_NAME + "."
//                                + SurakshaContract.AccountEntry.COLUMN_ACCOUNT_NUMBER}, null, null, null);
//        if (cursorAccountNumbers != null) {
//            while (cursorAccountNumbers.moveToNext()) {
//                acNumbers.add(cursorAccountNumbers.getInt(0));
//            }
//            cursorAccountNumbers.close();
//        }
//        this.accountNumbers = acNumbers;
//        return acNumbers;
//    }

//    public List<Integer> getAccountNumbers() {
//        return accountNumbers;
//    }

    public byte[] getAvatar() {
        return ImageUtils.drawableToByteArray(avatar);
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = ImageUtils.byteToDrawable(Resources.getSystem(), avatar);
    }

    public Drawable getAvatarDrawable() {
        return avatar;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return WordUtils.toTitleCase(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return WordUtils.toTitleCase(alias);
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
        return WordUtils.toTitleCase(father);
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getSpouse() {
        return WordUtils.toTitleCase(spouse);
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public String getOccupation() {
        return WordUtils.toTitleCase(occupation);
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
        return WordUtils.toTitleCase(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNominee() {
        return WordUtils.toTitleCase(nominee);
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
        return WordUtils.toTitleCase(addressOfNominee);
    }

    public void setAddressOfNominee(String addressOfNominee) {
        this.addressOfNominee = addressOfNominee;
    }

    public boolean hasActiveLoan() {
        return hasActiveLoan;
    }

    public void setHasActiveLoan(boolean hasActiveLoan) {
        this.hasActiveLoan = hasActiveLoan;
    }

    public boolean isLoanBlocked() {
        return isLoanBlocked;
    }

    public void setLoanBlocked(boolean loanBlocked) {
        isLoanBlocked = loanBlocked;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(long closedAt) {
        this.closedAt = closedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", accountNo='" + accountNo + '\'' +
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
                ", hasActiveLoan=" + hasActiveLoan +
                ", isLoanBlocked=" + isLoanBlocked +
                ", isDeleted=" + isDeleted +
                ", closedAt=" + closedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

   /* protected static void setMemberInfo(final Context context, RelativeLayout rl, String name, String address, int accountNo, final Drawable avatar) {
//        RelativeLayout rl = findViewById(R.id.layoutMemberInfo);
//        if (rl == null) {
//            return;
//        }
        TextView lblMemberNameLoanIssue = rl.findViewById(R.id.lblNameMemberInfo);
        TextView lblAddressLoanIssue = rl.findViewById(R.id.lblAddressMemberInfo);
        TextView lblAccountNumberInLoanIssueTitle = rl.findViewById(R.id.lblAccountNumberMemberInfo);
        ImageView imgAvatarMemberLoanIssue = rl.findViewById(R.id.imgAvatarMemberInfo);

        //Set values of member name, address, avatar and account number
        lblMemberNameLoanIssue.setText(WordUtils.toTitleCase(name));
        lblAddressLoanIssue.setText(WordUtils.toTitleCase(address));
        lblAccountNumberInLoanIssueTitle.setText(String.valueOf(accountNo));
        if (avatar != null) {
            imgAvatarMemberLoanIssue.setImageDrawable(avatar);
            imgAvatarMemberLoanIssue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AvatarActivity.class);
                    intent.putExtra("avatar", ImageUtils.drawableToByteArray(avatar));
                    context.startActivity(intent);
                }
            });
        } else {
            int color = context.getResources().getColor(R.color.colorPrimaryLight);
            imgAvatarMemberLoanIssue.setImageDrawable(new LetterAvatar(context,
                    color, name.substring(0, 1).toUpperCase(), 24));
        }

    }*/

    public void setMemberInfo(final Context context, RelativeLayout rl) {
        if (rl == null) return;
        TextView lblNameMemberInfo = rl.findViewById(R.id.lblNameMemberInfo);
        TextView lblAddressMemberInfo = rl.findViewById(R.id.lblAddressMemberInfo);
        TextView lblAccountNumberMemberInfo = rl.findViewById(R.id.lblAccountNumberMemberInfo);
        ImageView imgAvatarMemberInfo = rl.findViewById(R.id.imgAvatarMemberInfo);

        //Set values of member name, address, avatar and account number
        lblNameMemberInfo.setText(WordUtils.toTitleCase(name));
        lblAddressMemberInfo.setText(WordUtils.toTitleCase(address));
        lblAccountNumberMemberInfo.setText(String.valueOf(accountNo));
        if (avatar != null) {
            imgAvatarMemberInfo.setImageDrawable(avatar);
            imgAvatarMemberInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AvatarActivity.class);
                    intent.putExtra("avatar", ImageUtils.drawableToByteArray(avatar));
                    context.startActivity(intent);
                }
            });
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            int avatarPixel = 52;
            int sizeDp = (int) (avatarPixel * density);
            Bitmap bitmap = new LetterTileProvider(context).getLetterTile(
                    WordUtils.toTitleCase(name), String.valueOf(accountNo), sizeDp);
            imgAvatarMemberInfo.setImageBitmap(bitmap);
        }

    }

    public boolean isSmsEnabled() {
        return isSmsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        isSmsEnabled = smsEnabled;
    }

    public interface MemberQuery {
//        String[] PROJECTION = {
//                SurakshaContract.MemberEntry.TABLE_NAME + "." +SurakshaContract.MemberEntry._ID,
//                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_NAME,
//                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO,
//                SurakshaContract.MemberEntry.COLUMN_ADDRESS,
//                SurakshaContract.MemberEntry.COLUMN_HAS_LOAN,
//                SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED,
//                SurakshaContract.MemberEntry.COLUMN_AVATAR
//        };
//        int COLM_ID = 0;
//        int COLM_NAME = 1;
//        int COLM_ACCOUNT_NO = 2;
//        int COLM_ADDRESS = 3;
//        int COLM_HAS_LOAN = 4;
//        int COLM_IS_LOAN_BLOCKED = 5;
//        int COLM_AVATAR = 6;


        String[] PROJECTION = {
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_NAME,
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
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_REMARKS,
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_CLOSED_AT,
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_CREATED_AT,
                SurakshaContract.MemberEntry.TABLE_NAME + "." + SurakshaContract.MemberEntry.COLUMN_UPDATED_AT,
                SurakshaContract.MemberEntry.COLUMN_AVATAR,
                SurakshaContract.MemberEntry.COLUMN_ACCOUNT_NO,
                SurakshaContract.MemberEntry._ID,
                SurakshaContract.MemberEntry.COLUMN_IS_LOAN_BLOCKED,
                SurakshaContract.MemberEntry.COLUMN_HAS_LOAN,
                SurakshaContract.MemberEntry.COLUMN_IS_SMS_ENABLED
        };

        int COL_NAME = 0;
        int COL_ALIAS = 1;
        int COL_GENDER = 2;
        int COL_FATHER = 3;
        int COL_SPOUSE = 4;
        int COL_OCCUPATION = 5;
        int COL_AGE = 6;
        int COL_MOBILE = 7;
        int COL_ADDRESS = 8;
        int COL_NOMINEE = 9;
        int COL_RELATION_WITH_NOMINEE = 10;
        int COL_ADDRESS_OF_NOMINEE = 11;
        int COL_REMARKS = 12;
        int COL_CLOSED_AT = 13;
        int COL_CREATED_AT = 14;
        int COL_UPDATED_AT = 15;
        int COL_AVATAR = 16;
        int COL_ACCOUNT_NO = 17;
        int COL_ID = 18;
        int COL_IS_LOAN_BLOCKED = 19;
        int COL_HAS_LOAN = 20;
        int COL_IS_SMS_ENABLED = 21;

    }


}
