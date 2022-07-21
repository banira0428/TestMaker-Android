package com.example.ui.logger

enum class LogEvent(val eventName: String) {
    HOME_SCREEN_OPEN("HOME_SCREEN_OPEN"),
    HOME_BUTTON_CREATE_WORKBOOK("HOME_BUTTON_CREATE_WORKBOOK"),
    CREATE_WORKBOOK_SCREEN_OPEN("CREATE_WORKBOOK_SCREEN_OPEN"),
    HOME_BUTTON_SET_COLOR_TO_WORKBOOK("HOME_BUTTON_SET_COLOR_TO_WORKBOOK"),
    HOME_BUTTON_SET_FOLDER_TO_WORKBOOK("HOME_BUTTON_SET_FOLDER_TO_WORKBOOK"),
    CREATE_FOLDER_SCREEN_OPEN("CREATE_FOLDER_SCREEN_OPEN"),
    HOME_BUTTON_STORE_FOLDER("HOME_BUTTON_STORE_FOLDER"),
    HOME_BUTTON_IMPORT_WORKBOOK("HOME_BUTTON_IMPORT_WORKBOOK"),
    HOME_SUCCESS_IMPORT_WORKBOOK("HOME_SUCCESS_IMPORT_WORKBOOK"),
    HOME_BUTTON_HELP_FOR_IMPORT_WORKBOOK("HOME_BUTTON_HELP_FOR_IMPORT_WORKBOOK"),
    HOME_BUTTON_STORE_WORKBOOK("HOME_BUTTON_STORE_WORKBOOK"),
    HOME_ITEM_OPEN_FOLDER("HOME_ITEM_OPEN_FOLDER"),
    HOME_ITEM_OPERATE_FOLDER("HOME_ITEM_OPEARATE_FOLDER"),
    HOME_BUTTON_EDIT_FOLDER("HOME_BUTTON_EDIT_FOLDER"),
    HOME_BUTTON_UPDATE_FOLDER("HOME_BUTTON_UPDATE_FOLDER"),
    HOME_BUTTON_DELETE_FOLDER("HOME_BUTTON_DELETE_FOLDER"),
    HOME_ITEM_OPERATE_WORKBOOK("HOME_ITEM_OPERATE_WORKBOOK"),
    HOME_BUTTON_PLAY_WORKBOOK("HOME_BUTTON_PLAY_WORKBOOK"),
    HOME_BUTTON_EDIT_WORKBOOK("HOME_BUTTON_EDIT_WORKBOOK"),
    HOME_BUTTON_DELETE_WORKBOOK("HOME_BUTTON_DELETE_WORKBOOK"),
    HOME_BUTTON_SHARE_WORKBOOK("HOME_BUTTON_SHARE_WORKBOOK"),
    HOME_TAB_ACCOUNT("HOME_TAB_ACCOUNT"),
    HOME_TAB_DEVICE("HOME_TAB_DEVICE"),
    HOME_BUTTON_UPLOAD_WORKBOOK("HOME_BUTTON_UPLOAD_WORKBOOK"),
    HOME_REFRESH_UPLOADED_WORKBOOK("HOME_REFRESH_UPLOADED_WORKBOOK"),
    HOME_BUTTON_LOGIN("HOME_BUTTON_LOGIN"),
    HOME_ITEM_OPERATE_UPLOADED_WORKBOOK("HOME_ITEM_OPERATE_UPLOADED_WORKBOOK"),
    HOME_BUTTON_DOWNLOAD_UPLOADED_WORKBOOK("HOME_BUTTON_DOWNLOAD_UPLOADED_WORKBOOK"),
    HOME_BUTTON_DELETE_UPLOADED_WORKBOOK("HOME_BUTTON_DELETE_UPLOADED_WORKBOOK"),
    HOME_BUTTON_SHARE_UPLOADED_WORKBOOK("HOME_BUTTON_SHARE_UPLOADED_WORKBOOK"),
    ANSWER_SCREEN_OPEN("ANSWER_SCREEN_OPEN"),
    ANSWER_VIEW_APPEAR("ANSWER_VIEW_APPEAR"),
    ANSWER_ERROR_QUESTIONS("ANSWER_ERROR_QUESTIONS"),
    ANSWER_SHOW_QUESTION("ANSWER_SHOW_QUESTION"),
    ANSWER_BUTTON_END("ANSWER_BUTTON_END"),
    RESULT_SCREEN_OPEN("RESULT_SCREEN_OPEN"),
    RESULT_BUTTON_BACK_HOME("RESULT_BUTTON_BACK_HOME"),
    RESULT_BUTTON_RETRY("RESULT_BUTTON_RETRY"),
    QUESTIONS_SCREEN_OPEN("QUESTIONS_SCREEN_OPEN"),
    QUESTIONS_BUTTON_CREATE_QUESTION("QUESTIONS_BUTTON_CREATE_QUESTION"),
    QUESTIONS_BUTTON_SELECT("QUESTIONS_BUTTON_SELECT"),
    QUESTIONS_BUTTON_MOVE_QUESTIONS("QUESTIONS_BUTTON_MOVE_QUESTIONS"),
    QUESTIONS_BUTTON_COPY_QUESTIONS("QUESTIONS_BUTTON_COPY_QUESTIONS"),
    QUESTIONS_BUTTON_DELETE_QUESTIONS("QUESTIONS_BUTTON_DELETE_QUESTIONS"),
    QUESTIONS_ITEM_OPERATE_QUESTION("QUESTIONS_ITEM_OPERATE_QUESTION"),
    QUESTIONS_BUTTON_EDIT_QUESTION("QUESTIONS_BUTTON_EDIT_QUESTION"),
    QUESTIONS_BUTTON_MOVE_QUESTION("QUESTIONS_BUTTON_MOVE_QUESTION"),
    QUESTIONS_BUTTON_COPY_QUESTION("QUESTIONS_BUTTON_COPY_QUESTION"),
    QUESTIONS_BUTTON_DELETE_QUESTION("QUESTIONS_BUTTON_DELETE_QUESTION"),
    QUESTIONS_BUTTON_EDIT_WORKBOOK("QUESTIONS_BUTTON_EDIT_WORKBOOK"),
    QUESTIONS_BUTTON_UPDATE_WORKBOOK("QUESTIONS_BUTTON_UPDATE_WORKBOOK"),
    QUESTIONS_BUTTON_SEARCH_QUESTION("QUESTIONS_BUTTON_SEARCH_QUESTION"),
    EDIT_WORKBOOK_SCREEN("EDIT_WORKBOOK_SCREEN"),
    CREATE_QUESTION_SCREEN_OPEN("CREATE_QUESTION_SCREEN_OPEN"),
    QUESTIONS_BUTTON_STORE_QUESTION("QUESTIONS_BUTTON_STORE_QUESTION"),
    EDIT_QUESTION_SCREEN_OPEN("EDIT_QUESTION_SCREEN_OPEN"),
    QUESTIONS_BUTTON_UPDATE_QUESTION("QUESTIONS_BUTTON_UPDATE_QUESTION"),
    SEARCH_SCREEN_OPEN("SEARCH_SCREEN_OPEN"),
    SEARCH_BUTTON_SEARCH_PUBLISHED_WORKBOOK("SEARCH_BUTTON_SEARCH_PUBLISHED_WORKBOOK"),
    SEARCH_BUTTON_UPLOAD_WORKBOOK("SEARCH_BUTTON_UPLOAD_WORKBOOK"),
    SEARCH_ITEM_OPERATE_WORKBOOK("SEARCH_ITEM_OPERATE_WORKBOOK"),
    SEARCH_BUTTON_DOWNLOAD_WORKBOOK("SEARCH_BUTTON_DOWNLOAD_WORKBOOK"),
    SEARCH_BUTTON_SHARE_WORKBOOK("SEARCH_BUTTON_SHARE_WORKBOOK"),
    GROUP_SCREEN_OPEN("GROUP_SCREEN_OPEN"),
    GROUP_BUTTON_CREATE_GROUP("GROUP_BUTTON_CREATE_GROUP"),
    GROUP_BUTTON_STORE_GROUP("GROUP_BUTTON_STORE_GROUP"),
    GROUP_ITEM_OPEN_GROUP("GROUP_ITEM_OPEN_GROUP"),
    GROUP_BUTTON_UPLOAD_WORKBOOK("GROUP_BUTTON_UPLOAD_WORKBOOK"),
    GROUP_ITEM_OPERATE_WORKBOOK("GROUP_ITEM_OPERATE_WORKBOOK"),
    GROUP_BUTTON_DOWNLOAD_WORKBOOK("GROUP_BUTTON_DOWNLOAD_WORKBOOK"),
    GROUP_BUTTON_SHARE_WORKBOOK("GROUP_BUTTON_SHARE_WORKBOOK"),
    GROUP_BUTTON_HISTORY_WORKBOOK("GROUP_BUTTON_HISTORY_WORKBOOK"),
    GROUP_BUTTON_DELETE_WORKBOOK("GROUP_BUTTON_DELETE_WORKBOOK"),
    GROUP_BUTTON_JOIN_GROUP("GROUP_BUTTON_JOIN_GROUP"),
    GROUP_BUTTON_EDIT_GROUP("GROUP_BUTTON_EDIT_GROUP"),
    GROUP_BUTTON_EXIT_GROUP("GROUP_BUTTON_EXIT_GROUP"),
    GROUP_BUTTON_INVITE_GROUP("GROUP_BUTTON_INVITE_GROUP"),
    GROUP_DETAILS_SCREEN_OPEN("GROUP_DETAILS_SCREEN_OPEN"),
    HISTORY_SCREEN_OPEN("HISTORY_SCREEN_OPEN"),
    SETTINGS_SCREEN_OPEN("SETTINGS_SCREEN_OPEN"),
    SETTINGS_BUTTON_EDIT_THEME_COLOR("SETTINGS_BUTTON_EDIT_THEME_COLOR"),
    SETTINGS_BUTTON_EDIT_USER_NAME("SETTINGS_BUTTON_EDIT_USER_NAME"),
    SETTINGS_BUTTON_LOGOUT("SETTINGS_BUTTON_LOGOUT"),
    SETTINGS_BUTTON_LOGIN("SETTINGS_BUTTON_LOGIN"),
    SETTINGS_BUTTON_REMOVE_AD("SETTINGS_BUTTON_REMOVE_AD"),
    SETTINGS_BUTTON_FAQ("SETTINGS_BUTTON_FAQ"),
}