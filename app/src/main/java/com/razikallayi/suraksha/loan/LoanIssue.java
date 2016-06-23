package com.razikallayi.suraksha.loan;

import android.content.ContentValues;

import com.razikallayi.suraksha.data.SurakshaContract;
import com.razikallayi.suraksha.member.Member;

/**
 * Created by Razi Kallayi on 19-06-2016 08:37.
 */
public class LoanIssue {

    private long id;
    private int accountNumber;
    private double amount;
    private int securityAccountNo;
    private String purpose;
    private int loanInstalmentTimes = 10;
    private double loanInstalmentAmount;
    private String officeStatement;
    private Member member;
    private Member securityMember;
    private long closedAt;
    private long createdAt;
    private long updatedAt;

    public LoanIssue(int accountNumber, double amount, String purpose, int securityAccountNo, int loanInstalmentTimes, String officeStatement) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.purpose = purpose;
        this.securityAccountNo = securityAccountNo;
        this.loanInstalmentTimes = loanInstalmentTimes;
        this.loanInstalmentAmount = amount/loanInstalmentTimes;
        this.officeStatement = officeStatement;
    }


    public static ContentValues getLoanIssuedContentValues(LoanIssue loanIssue) {
        ContentValues values = new ContentValues();

        values.put(SurakshaContract.LoanIssueEntry.COLUMN_FK_ACCOUNT_NUMBER, loanIssue.accountNumber);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_AMOUNT, loanIssue.amount);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_TIMES, loanIssue.loanInstalmentTimes);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_LOAN_INSTALMENT_AMOUNT, loanIssue.loanInstalmentAmount);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_PURPOSE, loanIssue.purpose);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_SECURITY_ACCOUNT_NUMBER, loanIssue.securityAccountNo);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_OFFICE_STATEMENT, loanIssue.officeStatement);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_CLOSED_AT, loanIssue.closedAt);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_CREATED_AT, loanIssue.createdAt);
        values.put(SurakshaContract.LoanIssueEntry.COLUMN_UPDATED_AT, loanIssue.updatedAt);

        return values;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Member getSecurityMember() {
        return securityMember;
    }

    public void setSecurityMember(Member securityMember) {
        this.securityMember = securityMember;
    }

    public String getOfficeStatement() {
        return officeStatement;
    }

    public void setOfficeStatement(String officeStatement) {
        this.officeStatement = officeStatement;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public int getSecurityAccountNo() {
        return securityAccountNo;
    }

    public void setSecurityAccountNo(int securityAccountNo) {
        this.securityAccountNo = securityAccountNo;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLoanInstalmentTimes() {
        return loanInstalmentTimes;
    }

    public void setLoanInstalmentTimes(int loanInstalmentTimes) {
        this.loanInstalmentTimes = loanInstalmentTimes;
    }

    public double getLoanInstalmentAmount() {
        return loanInstalmentAmount;
    }

    public void setLoanInstalmentAmount(double loanInstalmentAmount) {
        this.loanInstalmentAmount = loanInstalmentAmount;
    }
}
