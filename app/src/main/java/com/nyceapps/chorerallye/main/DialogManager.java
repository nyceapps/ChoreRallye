package com.nyceapps.chorerallye.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.nyceapps.chorerallye.R;

import java.util.HashMap;
import java.util.Map;

import static com.nyceapps.chorerallye.main.Constants.DIALOG_TYPE_SAVE;

/**
 * Created by lugosi on 09.04.17.
 */

public class DialogManager {
    private final static String TAG = DialogManager.class.getSimpleName();

    private Context context;

    private Map<String, AlertDialog> dialogs;
    private Map<String, Integer> counts;


    public DialogManager(Context pContext) {
        context = pContext;

        dialogs = new HashMap<>();
        counts = new HashMap<>();
    }


    public void showSavingDataDialog() {
        showDialog(DIALOG_TYPE_SAVE);
    }

    public void hideSavingDataDialog() {
        hideDialog(DIALOG_TYPE_SAVE);
    }

    private void showDialog(String pDialogType) {
        Integer count = counts.get(pDialogType);
        if (count == null) {
            count = 0;
        }
        Log.d(TAG, String.format("%s / showDialog / %s count = [%d]", context.getClass().getSimpleName(), pDialogType, count));
        if (count == 0) {
            AlertDialog dialog = getDialogByType(pDialogType);
            dialogs.put(pDialogType, dialog);
        }
        count++;
        counts.put(pDialogType, count);
    }

    private void hideDialog(String pDialogType) {
        Integer count = counts.get(pDialogType);
        Log.d(TAG, String.format("%s / hideDialog / %s count = [%d]", context.getClass().getSimpleName(), pDialogType, count));
        if (count != null) {
            count--;
            counts.put(pDialogType, count);
            if (count == 0) {
                AlertDialog dialog = dialogs.get(pDialogType);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }
    }

    private AlertDialog getDialogByType(String pDialogType) {
        AlertDialog dialog = null;

        switch (pDialogType) {
            case DIALOG_TYPE_SAVE:
                dialog = ProgressDialog.show(context, context.getString(R.string.dialog_text_saving_data), context.getString(R.string.dialog_text_please_wait), true);
                Log.d(TAG, String.format("%s / getDialogByType / %s dialog = [%s]", context.getClass().getSimpleName(), pDialogType, dialog.getClass().getSimpleName()));
                break;
        }

        return dialog;
    }
}
