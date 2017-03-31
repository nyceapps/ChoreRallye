package com.nyceapps.chorerallye.main;

import java.util.regex.Pattern;

/**
 * Created by lugosi on 06.02.17.
 */

public interface Constants {
    int CHORE_COLUMNS = 3;
    float MEMBER_IMAGE_CORNER_RADIUS = 45;

    String EXTRA_MESSAGE_UID = "com.nyceapps.chorerallye.MESSAGE_UID";
    String EXTRA_MESSAGE_NAME = "com.nyceapps.chorerallye.MESSAGE_NAME";
    String EXTRA_MESSAGE_ORIGINAL_NAME = "com.nyceapps.chorerallye.MESSAGE_ORIGINAL_NAME";
    String EXTRA_MESSAGE_VALUE = "com.nyceapps.chorerallye.MESSAGE_VALUE";
    String EXTRA_MESSAGE_ORIGINAL_VALUE = "com.nyceapps.chorerallye.MESSAGE_ORIGINAL_VALUE";
    String EXTRA_MESSAGE_FILE_STRING = "com.nyceapps.chorerallye.MESSAGE_FILE_STRING";
    String EXTRA_MESSAGE_ADD_NOTE_INSTANTLY = "com.nyceapps.chorerallye.EXTRA_MESSAGE_ADD_NOTE_INSTANTLY";

    int REQUEST_CODE_ADD_MEMBER = 1;
    int REQUEST_CODE_EDIT_MEMBER = 2;
    int REQUEST_CODE_ADD_CHORE = 3;
    int REQUEST_CODE_EDIT_CHORE = 4;
    int REQUEST_CODE_CAPTURE_IMAGE_FROM_CAMERA = 5;
    int REQUEST_CODE_SCAN_QR_CODE = 6;
    int REQUEST_CODE_PERMISSION_REQUEST_CAMERA = 7;
    int REQUEST_CODE_MANAGE_PREFERENCES = 8;
    int REQUEST_CODE_CREATE_BACKUP = 9;
    int REQUEST_CODE_RESTORE_BACKUP = 10;

    int CONTEXT_MENU_ACTION_EDIT = 0;
    int CONTEXT_MENU_ACTION_REMOVE = 1;

    String DATABASE_SUBPATH_SETTINGS = "settings";
    String DATABASE_SUBPATH_MEMBERS = "members";
    String DATABASE_SUBPATH_CHORES = "chores";
    String DATABASE_SUBPATH_RACE = "race";
    String DATABASE_SUBPATH_HISTORY = "history";
    String DATABASE_SUBPATH_META = "meta";
    String DATABASE_SUBPATH_ITEMS = "items";

    String DATABASE_CHILD_KEY_MEMBER_NAME = "memberName";
    String DATABASE_CHILD_KEY_CHORE_NAME = "choreName";
    String DATABASE_CHILD_KEY_CHORE_VALUE = "choreValue";

    String DATABASE_KEY_ORDER_KEY = "orderKey";
    String DATABASE_KEY_DATE_STARTED = "dateStarted";
    String DATABASE_KEY_DATE_ENDING = "dateEnding";

    String PREF_KEY_HOUSEHOLD_ID = "pref_key_household_id";
    String PREF_KEY_HOUSEHOLD_NAME = "pref_key_household_name";
    String PREF_KEY_LAST_DISPLAYED_RACE_ITEM_UID = "pref_key_last_displayed_race_item_uid";
    String PREF_KEY_WINNING_PERCENTAGE = "pref_key_winning_percentage";
    String PREF_KEY_LENGTH_OF_RALLYE_IN_DAYS = "pref_key_length_of_rallye_in_days";
    String PREF_KEY_INSTANTLY_ADD_RACE_ITEM_NOTE = "pref_key_instantly_add_race_item_note";

    String PREF_KEY_LOCAL_HISTORY_COUNT = "pref_key_local_history_count";
    String PREF_KEY_PREFIX_LOCAL_HISTORY_ENTRY = "pref_key_prefix_local_history_entry_";

    String PREF_KEY_DISPLAYED_RACE_ITEMS_COUNT = "pref_key_displayed_race_items_count";
    String PREF_KEY_PREFIX_DISPLAYED_RACE_ITEMS_ENTRY = "pref_key_prefix_displayed_race_items_entry_";

    String DISPLAY_MODE_RALLYE = "RALLYE";
    String DISPLAY_MODE_LOG = "LOG";

    String SETTINGS_DEFAULT_VALUE_DISPLAY_MODE = DISPLAY_MODE_RALLYE;
    int SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE = 67;
    int SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS = 30;
    boolean SETTINGS_DEFAULT_VALUE_INSTANTLY_ADD_RACE_ITEM_NOTE = false;

    String HOUSEHOLD_ID_INFIX = "@@@";
    Pattern HOUSEHOLD_NAME_ID_PATTERN = Pattern.compile("(.+)@@@(.+)");
    Pattern HOUSEHOLD_AT_NAME_ID_PATTERN_AT = Pattern.compile("@@@(.+)@@@(.+)@@@");

    int LENGTH_OF_RACE_IN_DAYS_FOR_LOG_MODE = 3650;

    String BACKUP_FILENAME_STRING_PATTERN = "%s_backup_%s.zip";
    String BACKUP_EMBEDDED_FILENAME = "backup.json";

    boolean DEFAULT_VALUE_ADD_NOTE_INSTANTLY = false;
}