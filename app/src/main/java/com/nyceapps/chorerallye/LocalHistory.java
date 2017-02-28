package com.nyceapps.chorerallye;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.FILE_NAME_LOCAL_HISTORY;

/**
 * Created by lugosi on 26.02.17.
 */

public class LocalHistory {
    private final static String TAG = LocalHistory.class.getSimpleName();

    private static final Type RACE_ITEM_LIST_TYPE = new TypeToken<List<RaceItem>>() {}.getType();

    private final Context context;

    List<RaceItem> entries;

    public LocalHistory(Context pContext) {
        context = pContext;
        load();
    }

    public void add(RaceItem pEntry) {
        entries.add(pEntry);
    }

    public RaceItem undo() {
        if (entries.size() > 0) {
            return entries.remove(entries.size() - 1);
        }

        return null;
    }

    public List<RaceItem> getEntries() {
        return entries;
    }

    public void load() {
        /*
        FileInputStream is = null;
        try {
            is = context.openFileInput(FILE_NAME_LOCAL_HISTORY);
            JsonReader reader = new JsonReader(new FileReader(is.getFD()));
            Gson gson = new Gson();
            entries = gson.fromJson(reader, RACE_ITEM_LIST_TYPE);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (entries == null) {
                entries = new ArrayList<>();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        */

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
            if (!TextUtils.isEmpty(json)) {
                entries = new Gson().fromJson(json, new TypeToken<List<RaceItem>>(){}.getType());
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.d(TAG, "Cannot read file: " + e.toString());
        } finally {
            if (entries == null) {
                entries = new ArrayList<>();
            }

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
        /*
        FileOutputStream os = null;
        try {
            os = context.openFileOutput(FILE_NAME_LOCAL_HISTORY, Context.MODE_PRIVATE);
            FileWriter writer = new FileWriter(os.getFD());
            Gson gson = new GsonBuilder().create();
            gson.toJson(entries, writer);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.d(TAG, "Cannot close input stream: " + e.toString());
                }
            }
        }
        */

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
