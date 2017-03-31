package com.nyceapps.chorerallye.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.chore.ChoreItem;
import com.nyceapps.chorerallye.member.MemberItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;

import static com.nyceapps.chorerallye.main.Constants.HOUSEHOLD_AT_NAME_ID_PATTERN_AT;
import static com.nyceapps.chorerallye.main.Constants.HOUSEHOLD_ID_INFIX;
import static com.nyceapps.chorerallye.main.Constants.HOUSEHOLD_NAME_ID_PATTERN;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_HOUSEHOLD_ID;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_HOUSEHOLD_NAME;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_INSTANTLY_ADD_RACE_ITEM_NOTE;
import static com.nyceapps.chorerallye.main.Constants.SETTINGS_DEFAULT_VALUE_INSTANTLY_ADD_RACE_ITEM_NOTE;

/**
 * Created by bela on 08.02.17.
 */

public final class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private Utils() {}

    public static int calculatePercentage(int pPart, int pTotal) {
        return Math.round(pTotal > 0 ? (pPart * 100f) / pTotal : 0);
    }

    public static String makeRaceItemText(MemberItem pMember, ChoreItem pChore, Context pContext, boolean pIncludePoints) {
        return makeRaceItemText(pMember.getName(), pChore.getName(), pChore.getValue(), pContext, pIncludePoints);
    }

    public static String makeRaceItemText(String pMemberName, String pChoreName, int pChoreValue, Context pContext, boolean pIncludePoints) {
        String raceItemString = null;
        if (pIncludePoints) {
            raceItemString = String.format(pContext.getString(R.string.race_item_text_member_points_for_chore), pMemberName, pChoreValue, pChoreName);
        } else {
            raceItemString = String.format(pContext.getString(R.string.race_item_text_member_chore), pMemberName, pChoreName);
        }
        return String.format(raceItemString, pMemberName, pChoreValue, pChoreName);
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

    public static int calculateMaxMemberTextWidth(List<MemberItem> pMembers, Context pContext) {
        int maxMemberTextWidth = 0;

        TextView textView = new TextView(pContext);
        for (MemberItem memberItem : pMembers) {
            String memberText = memberItem.getName() + " (100%)";
            Rect bounds = new Rect();
            Paint textPaint = textView.getPaint();
            textPaint.getTextBounds(memberText, 0, memberText.length(), bounds);
            maxMemberTextWidth = Math.max(bounds.width(), maxMemberTextWidth);
        }

        return maxMemberTextWidth;
    }

    public static String[] getHouseholdIdWithName(String pHouseholdName) {
        String householdName = pHouseholdName;
        String householdUuidId = null;

        Matcher matcher = HOUSEHOLD_AT_NAME_ID_PATTERN_AT.matcher(pHouseholdName);
        if (matcher.matches() && matcher.groupCount() == 2) {
            householdName = matcher.group(1);
            householdUuidId = matcher.group(2);
        } else {
            if (!TextUtils.isEmpty(householdName)) {
                householdName = formatHouseholdName(householdName);
            }
        }
        if (TextUtils.isEmpty(householdUuidId)) {
            householdUuidId = UUID.randomUUID().toString();
        }
        String householdId = householdName + HOUSEHOLD_ID_INFIX + householdUuidId;

        return new String[]{householdId, householdName};
    }

    public static String getHouseholdId(Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        String householdId = sharedPreferences.getString(PREF_KEY_HOUSEHOLD_ID, null);
        Log.d(TAG, String.format("householdId = [%s]", householdId));
        return householdId;
    }

    public static String getHouseholdNameFromId(String pHouseholdId) {
        Matcher matcher = HOUSEHOLD_NAME_ID_PATTERN.matcher(pHouseholdId);
        if (matcher.matches() && matcher.groupCount() == 2) {
            String householdName = matcher.group(1);
            if (!TextUtils.isEmpty(householdName)) {
                householdName = formatHouseholdName(householdName);
            }
            return householdName;
        }

        return null;
    }

    private static String formatHouseholdName(String pHouseholdName) {
        String householdName = pHouseholdName;

        if (!TextUtils.isEmpty(householdName)) {
            householdName = householdName.trim().toUpperCase();

            StringBuilder householdNameSB = new StringBuilder();
            if(!Character.isJavaIdentifierStart(householdName.charAt(0))) {
                householdNameSB.append("_");
            }
            for (char c : householdName.toCharArray()) {
                if(!Character.isJavaIdentifierPart(c)) {
                    householdNameSB.append("_");
                } else {
                    householdNameSB.append(c);
                }
            }

            householdName = householdNameSB.toString();
        }

        return householdName;
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

    public static boolean isInstantlyAddRaceItemNote(Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        boolean isInstantlyAddRaceItemNote = sharedPreferences.getBoolean(PREF_KEY_INSTANTLY_ADD_RACE_ITEM_NOTE, SETTINGS_DEFAULT_VALUE_INSTANTLY_ADD_RACE_ITEM_NOTE);
        return isInstantlyAddRaceItemNote;
    }

    /*
    public static String getLastDisplayedRaceItemUid(Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        String lastDisplayedRaceItemUid = sharedPreferences.getString(PREF_KEY_LAST_DISPLAYED_RACE_ITEM_UID, null);
        Log.d(TAG, String.format("lastDisplayedRaceItemUid = [%s]", lastDisplayedRaceItemUid));
        return lastDisplayedRaceItemUid;
    }

    public static void setLastDisplayedRaceItemUid(String pLastDisplayedRaceItemUid, Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_LAST_DISPLAYED_RACE_ITEM_UID, pLastDisplayedRaceItemUid);
        editor.commit();
    }
    */

    public static Date getDateEndingForRace(Date pDateStarted, int pLengthOfRallyeInDays) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.setTime(pDateStarted);
        cal.add(Calendar.DAY_OF_MONTH, pLengthOfRallyeInDays);
        return cal.getTime();
    }

    public static String getRaceInfoText(RallyeData pData, Context pContext) {
        String raceInfoText = pContext.getString(R.string.race_info_text_not_running);
        if (pData != null) {
            if (pData.getSettings().isRunning()) {
                Date dateStarted = pData.getRace().getDateStarted();
                Date dateEnding = pData.getRace().getDateEnding();

                if (dateStarted != null && dateEnding != null) {
                    java.text.DateFormat dateFormat = DateFormat.getDateFormat(pContext);
                    String dateStartedStr = dateFormat.format(dateStarted);
                    String dateEndingStr = dateFormat.format(dateEnding);
                    String datePeriodStr = dateStartedStr + " - " + dateEndingStr;

                    long startTime = new Date().getTime();
                    long endTime = dateEnding.getTime();
                    long diffTime = endTime - startTime;
                    long daysLeft = (diffTime / (1000 * 60 * 60 * 24)) + 1;

                    raceInfoText = String.format(pContext.getString(R.string.race_info_text_running), datePeriodStr, daysLeft);
                }
            }
        }
        return raceInfoText;
    }
}
