package com.nyceapps.chorerallye;

/**
 * Created by lugosi on 06.02.17.
 */

public interface Constants {
    int CHORE_COLUMNS = 3;
    float MEMBER_IMAGE_CORNER_RADIUS = 45;

    String EXTRA_MESSAGE_UID = "com.nyceapps.chorerallye.MESSAGE_UID";
    String EXTRA_MESSAGE_NAME = "com.nyceapps.chorerallye.MESSAGE_NAME";
    String EXTRA_MESSAGE_VALUE = "com.nyceapps.chorerallye.MESSAGE_VALUE";
    String EXTRA_MESSAGE_FILE_STRING = "com.nyceapps.chorerallye.MESSAGE_FILE_STRING";

    int REQUEST_CODE_ADD_MEMBER = 1;
    int REQUEST_CODE_EDIT_MEMBER = 2;
    int REQUEST_CODE_ADD_CHORE = 3;
    int REQUEST_CODE_EDIT_CHORE = 4;
    int REQUEST_CODE_CAPTURE_IMAGE_FROM_CAMERA = 5;
    //int REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY = 6;

    int CONTEXT_MENU_ACTION_EDIT = 0;
    int CONTEXT_MENU_ACTION_REMOVE = 1;

    String DATABASE_SUBPATH_MEMBERS = "members";
    String DATABASE_SUBPATH_CHORES = "chores";
    String DATABASE_SUBPATH_RACE = "race";

    int IMAGE_WIDTH = 64;
    int IMAGE_HEIGHT = 64;

    String PREFS_FILE_NAME = "com_nyceapps_chorerallye_preferences";
    String PREFS_KEY_HOUSEHOLD_NAME = "com.nyceapps.chorerallye.PREFS_KEY_HOUSEHOLD_NAME";
}
