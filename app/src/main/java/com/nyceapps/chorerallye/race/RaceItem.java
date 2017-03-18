package com.nyceapps.chorerallye.race;

import java.util.Date;

/**
 * Created by lugosi on 18.02.17.
 */

public class RaceItem {
    private String uid;
    private Date date;
    private String memberUid;
    private String memberName;
    private String choreUid;
    private String choreName;
    private int choreValue;

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
