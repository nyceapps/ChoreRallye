package com.nyceapps.chorerallye;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;

import com.google.firebase.database.Exclude;

import static com.nyceapps.chorerallye.Constants.MEMBER_IMAGE_CORNER_RADIUS;

/**
 * Created by lugosi on 07.02.17.
 */

public class MemberItem {
    private String uid;
    private String name;
    private String imageString;
    protected Drawable imageDrawable;

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
        if (!Utils.isEmptyString(pImageString)) {
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
}
