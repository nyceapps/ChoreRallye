package com.nyceapps.chorerallye;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.FILE_NAME_LOCAL_HISTORY;

/**
 * Created by lugosi on 26.02.17.
 */

public class LocalHistory {
    private final static String TAG = LocalHistory.class.getSimpleName();

    private final Context context;

    List<String> entries;

    public LocalHistory(Context pContext) {
        context = pContext;
        load();
    }

    public void add(String pEntry) {
        entries.add(pEntry);
    }

    public String undo() {
        if (entries.size() > 0) {
            return entries.remove(entries.size() - 1);
        }

        return null;
    }

    public List<String> getEntries() {
        return entries;
    }

    public void load() {
        entries = new ArrayList<>();

        InputStream is = null;
        BufferedReader br = null;
        try {
            is = context.openFileInput(FILE_NAME_LOCAL_HISTORY);
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            if (TextUtils.isEmpty(json)) {
                entries = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.d(TAG, "Cannot read file: " + e.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG, "Cannot close input stream: " + e.toString());
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.d(TAG, "Cannot close buffered reader: " + e.toString());
                }
            }
        }
    }

    public void save() {
        String json = new Gson().toJson(entries);
        FileOutputStream os = null;
        try {
            os = context.openFileOutput(FILE_NAME_LOCAL_HISTORY, Context.MODE_PRIVATE);
            os.write(json.getBytes());
        } catch (Exception e) {
            Log.d(TAG, "File exception: " + e.toString());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d(TAG, "Cannot close input stream: " + e.toString());
                }
            }
        }
    }
}
