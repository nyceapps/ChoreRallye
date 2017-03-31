package com.nyceapps.chorerallye.chore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;

import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.Utils;
import com.nyceapps.chorerallye.member.MemberItem;

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
}
