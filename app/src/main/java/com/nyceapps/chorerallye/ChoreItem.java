package com.nyceapps.chorerallye;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

/**
 * Created by lugosi on 06.02.17.
 */

public class ChoreItem extends MemberItem {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int pValue) {
        value = pValue;
    }

    @Override
    protected void initDrawable(String pImageString, Context pContext) {
        Bitmap placeHolderBitmap = BitmapFactory.decodeResource(pContext.getResources(), R.drawable.chore_placeholder);
        Bitmap choreBitmap;
        if (!Utils.isEmptyString(pImageString)) {
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
}
