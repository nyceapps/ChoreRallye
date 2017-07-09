package com.nyceapps.chorerallye.member;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.Utils;

import static com.nyceapps.chorerallye.main.Constants.DEFAULT_VALUE_ORDER_KEY;
import static com.nyceapps.chorerallye.main.Constants.MEMBER_IMAGE_CORNER_RADIUS;

/**
 * Created by lugosi on 07.02.17.
 */

public class MemberItem implements Parcelable {
    private String uid;
    private String name;
    private String imageString;
    private int orderKey = DEFAULT_VALUE_ORDER_KEY;
    protected transient Drawable imageDrawable;
    protected transient boolean hasNameUpdate;

    public MemberItem() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String pUid) {
        uid = pUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String pImageString) {
        imageString = pImageString;
    }

    public int getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(int pOrderKey) {
        orderKey = pOrderKey;
    }

    @Exclude
    public Drawable getDrawable(Context pContext) {
        if (imageDrawable == null) {
            initDrawable(imageString, pContext);
        }
        return imageDrawable;
    }

    protected void initDrawable(String pImageString, Context pContext) {
        Bitmap placeHolderBitmap = BitmapFactory.decodeResource(pContext.getResources(), R.drawable.member_placeholder);
        Bitmap memberBitmap;
        if (!TextUtils.isEmpty(pImageString)) {
            memberBitmap = Utils.convertStringToBitmap(pImageString);
            memberBitmap = Bitmap.createScaledBitmap(memberBitmap, placeHolderBitmap.getWidth(), placeHolderBitmap.getHeight(), true);
        } else {
            memberBitmap = placeHolderBitmap;
        }
        if (memberBitmap != null) {
            imageDrawable = RoundedBitmapDrawableFactory.create(pContext.getResources(), memberBitmap);
            ((RoundedBitmapDrawable) imageDrawable).setCornerRadius(MEMBER_IMAGE_CORNER_RADIUS);
        }
    }

    @Exclude
    public boolean hasNameUpdate() {
        return hasNameUpdate;
    }

    public void setNameUpdate(boolean pHasNameUpdate) {
        hasNameUpdate = pHasNameUpdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uid);
        out.writeString(name);
        out.writeString(imageString);
        out.writeInt(orderKey);
        out.writeByte((byte) (hasNameUpdate ? 1 : 0));
    }

    public static final Parcelable.Creator<MemberItem> CREATOR = new Parcelable.Creator<MemberItem>() {
        public MemberItem createFromParcel(Parcel in) {
            return new MemberItem(in);
        }

        public MemberItem[] newArray(int size) {
            return new MemberItem[size];
        }
    };

    public MemberItem(Parcel in) {
        uid = in.readString();
        name = in.readString();
        imageString = in.readString();
        orderKey = in.readInt();
        hasNameUpdate = in.readByte() != 0;
    }
}
