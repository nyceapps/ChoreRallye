package com.nyceapps.chorerallye.chore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.Utils;
import com.nyceapps.chorerallye.member.MemberItem;

import static android.R.string.no;
import static com.nyceapps.chorerallye.main.Constants.DEFAULT_VALUE_CHORE_VALUE;

/**
 * Created by lugosi on 06.02.17.
 */

public class ChoreItem extends MemberItem {
    private int value = DEFAULT_VALUE_CHORE_VALUE;
    private boolean instantlyAddNote;
    private transient boolean hasValueUpdate;

    public ChoreItem() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int pValue) {
        value = pValue;
    }

    public boolean isInstantlyAddNote() {
        return instantlyAddNote;
    }

    public void setInstantlyAddNote(boolean instantlyAddNote) {
        this.instantlyAddNote = instantlyAddNote;
    }

    @Override
    protected void initDrawable(String pImageString, Context pContext) {
        Bitmap placeHolderBitmap = BitmapFactory.decodeResource(pContext.getResources(), R.drawable.chore_placeholder);
        Bitmap choreBitmap;
        if (!TextUtils.isEmpty(pImageString)) {
            choreBitmap = Utils.convertStringToBitmap(pImageString);
            choreBitmap = Bitmap.createScaledBitmap(choreBitmap, placeHolderBitmap.getWidth(), placeHolderBitmap.getHeight(), true);
        } else {
            choreBitmap = placeHolderBitmap;
        }
        if (choreBitmap != null) {
            imageDrawable = RoundedBitmapDrawableFactory.create(pContext.getResources(), choreBitmap);
            ((RoundedBitmapDrawable) imageDrawable).setCircular(true);
        }
    }

    @Exclude
    public boolean hasValueUpdate() {
        return hasValueUpdate;
    }

    public void setValueUpdate(boolean pHasValueUpdate) {
        hasValueUpdate = pHasValueUpdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(value);
        out.writeByte((byte) (instantlyAddNote ? 1 : 0));
        out.writeByte((byte) (hasValueUpdate ? 1 : 0));
    }

    public static final Parcelable.Creator<ChoreItem> CREATOR = new Parcelable.Creator<ChoreItem>() {
        public ChoreItem createFromParcel(Parcel in) {
            return new ChoreItem(in);
        }

        public ChoreItem[] newArray(int size) {
            return new ChoreItem[size];
        }
    };

    public ChoreItem(Parcel in) {
        super(in);
        value = in.readInt();
        instantlyAddNote = in.readByte() != 0;
        hasValueUpdate = in.readByte() != 0;
    }
}
