package com.nyceapps.chorerallye;

import java.util.Date;

/**
 * Created by lugosi on 18.02.17.
 */

public class RaceItem {
    String uid;
    Date date;
    String memberUid;
    String memberName;
    String choreUid;
    String choreName;
    int choreValue;

    public String getUid() {
        return uid;
    }

    public void setUid(String pUid) {
        uid = pUid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String pMemberUid) {
        memberUid = pMemberUid;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String pMemberName) {
        memberName = pMemberName;
    }

    public String getChoreUid() {
        return choreUid;
    }

    public void setChoreUid(String pChoreUid) {
        choreUid = pChoreUid;
    }

    public String getChoreName() {
        return choreName;
    }

    public void setChoreName(String pChoreName) {
        choreName = pChoreName;
    }

    public int getChoreValue() {
        return choreValue;
    }

    public void setChoreValue(int pChoreValue) {
        choreValue = pChoreValue;
    }
}
