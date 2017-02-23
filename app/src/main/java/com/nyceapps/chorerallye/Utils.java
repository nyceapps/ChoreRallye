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

import static com.nyceapps.chorerallye.Constants.IMAGE_HEIGHT;
import static com.nyceapps.chorerallye.Constants.IMAGE_WIDTH;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_ID;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_NAME;

/**
 * Created by bela on 08.02.17.
 */

public class Utils {
    private static final String TAG = "Utils";

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

    /*
    public static String convertFileToString(File pFile) {
        String fileString = null;

        if (pFile != null) {
            String filePath = pFile.getPath();
            Bitmap fileBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(filePath), IMAGE_WIDTH, IMAGE_HEIGHT, false);
            if (fileBitmap != null) {
                ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                fileBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
                return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
            }
        }

        return fileString;
    }
    */

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

    public static String getHousehouldId(Context pContext) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(pContext);
        String householdId = sharedPrefs.getString(PREF_KEY_HOUSEHOLD_ID, null);
        Log.d(TAG, String.format("householdId = [%s]", householdId));
        return householdId;
    }
}
