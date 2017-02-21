package com.nyceapps.chorerallye;

/**
 * Created by lugosi on 18.02.17.
 */

public class RaceItem {
    String uid;
    String memberUid;
    String choreUid;
    int choreValue;

    public String getUid() {
        return uid;
    }

    public void setUid(String pUid) {
        uid = pUid;
    }

    public String getMemberUid() {
        return memberUid;
    }

    public void setMemberUid(String pMemberUid) {
        memberUid = pMemberUid;
    }

    public String getChoreUid() {
        return choreUid;
    }

    public void setChoreUid(String pChoreUid) {
        choreUid = pChoreUid;
    }

    public int getChoreValue() {
        return choreValue;
    }

    public void setChoreValue(int pChoreValue) {
        choreValue = pChoreValue;
    }
}
