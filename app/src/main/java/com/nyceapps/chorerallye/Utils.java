package com.nyceapps.chorerallye;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.nyceapps.chorerallye.Constants.HOUSEHOLD_ID_INFIX;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_ID;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_NAME;

/**
 * Created by bela on 08.02.17.
 */

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private Utils() {}

    public static int calculatePercentage(int pPart, int pTotal) {
        return Math.round(pTotal > 0 ? (pPart * 100f) / pTotal : 0);
    }

    public static String makeRacePointsText(RallyeData pData) {
        String pointsText = "";

        int totalPoints = pData.getRace().getTotalPoints();
        for (int i = 0; i < pData.getMembers().size(); i++) {
            MemberItem member = pData.getMembers().get(i);
            int memberPoints = pData.getRace().getPoints(member);
            int memberPercentage = Utils.calculatePercentage(memberPoints, totalPoints);
            pointsText += member.getName() + " - " + memberPoints + " (" + memberPercentage + "%)";
            if (i < pData.getMembers().size() - 1) {
                pointsText += "\n";
            }
        }

        return pointsText;
    }

    public static String convertFileToString(File pFile) {
        String fileString = null;

        if (pFile != null && pFile.exists()) {
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(pFile);
                byte[] buffer = new byte[8192];
                byte[] bytes;
                int bytesRead;
                outputStream = new ByteArrayOutputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                bytes = outputStream.toByteArray();
                fileString = Base64.encodeToString(bytes, Base64.DEFAULT);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        return fileString;
    }

    public static File createCameraFile(Context pContext) {
        File cameraImage = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = pContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            cameraImage = File.createTempFile(imageFileName, ".jpg",storageDir);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return cameraImage;
    }

    public static Bitmap convertStringToBitmap(String pString) {
        Bitmap stringBitmap = null;

        if (!TextUtils.isEmpty(pString)) {
            byte[] decodedBytes = Base64.decode(pString, Base64.DEFAULT);
            stringBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }

        return stringBitmap;
    }

    public static BitmapDrawable convertStringToBitmapDrawable(String pString, Context pContext) {
        BitmapDrawable stringBitmapDrawable = null;

        Bitmap stringBitmap = convertStringToBitmap(pString);
        if (stringBitmap != null) {
            stringBitmapDrawable = new BitmapDrawable(pContext.getResources(), stringBitmap);
        }

        return stringBitmapDrawable;
    }

    public static String getHouseholdId(Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        String householdId = sharedPreferences.getString(PREF_KEY_HOUSEHOLD_ID, null);
        Log.d(TAG, String.format("householdId = [%s]", householdId));
        return householdId;
    }

    public static void setHouseholdIdByName(String pHouseholdName, Context pContext) {
        String householdId = null;
        if (!TextUtils.isEmpty(pHouseholdName)) {
            householdId = pHouseholdName + HOUSEHOLD_ID_INFIX + UUID.randomUUID().toString();
        }
        setHouseholdId(householdId, pContext);
    }

    public static void setHouseholdId(String pHouseholdId, Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_HOUSEHOLD_ID, pHouseholdId);
        editor.commit();
    }

    public static void setHouseholdName(String pHouseholdName, Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_HOUSEHOLD_NAME, pHouseholdName);
        editor.commit();
    }
}
