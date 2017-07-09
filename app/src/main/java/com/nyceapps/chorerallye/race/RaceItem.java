package com.nyceapps.chorerallye.race;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by lugosi on 18.02.17.
 */

public class RaceItem implements Parcelable {
    private String uid;
    private Date date;
    private String memberUid;
    private String memberName;
    private String choreUid;
    private String choreName;
    private int choreValue;
    private String note;

    public RaceItem() {
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String pNote) {
        note = pNote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uid);
        out.writeValue(date);
        out.writeString(memberUid);
        out.writeString(memberName);
        out.writeString(choreUid);
        out.writeString(choreName);
        out.writeInt(choreValue);
        out.writeString(note);
    }

    public static final Parcelable.Creator<RaceItem> CREATOR = new Parcelable.Creator<RaceItem>() {
        public RaceItem createFromParcel(Parcel in) {
            return new RaceItem(in);
        }

        public RaceItem[] newArray(int size) {
            return new RaceItem[size];
        }
    };

    public RaceItem(Parcel in) {
        uid = in.readString();
        date = (Date) in.readValue(getClass().getClassLoader());
        memberUid = in.readString();
        memberName = in.readString();
        choreUid = in.readString();
        choreName = in.readString();
        choreValue = in.readInt();
        note = in.readString();
    }
}
