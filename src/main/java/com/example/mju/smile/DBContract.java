package com.example.mju.smile;

import android.provider.BaseColumns;

/**
 * Created by Jeong on 2017-11-28.
 */

public final class DBContract {

    public DBContract(){}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + Entry.TABLE_NAME + " ( " +
                    Entry._ID + " INTEGER PRIMARY KEY," +
                    Entry.COLUMN_NAME_MESSAGE_NAME + " text , " +
                    Entry.COLUMN_NAME_SENDER + " text , " +
                    Entry.COLUMN_NAME_LATITUDE + " text , " +
                    Entry.COLUMN_NAME_LONGTITUDE + " text , " +
                    Entry.COLUMN_NAME_PICTURE + " text , " +
                    Entry.COLUMN_NAME_DDAY + " text " + " ) ";

    public static final String SQL_DROP_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    public static final String SQL_SELECT_ENTRIES =
            "SELECT * FROM " + Entry.TABLE_NAME;

    public static final String SQL_DELETE_ENTRIES =
            "DELETE FROM " + Entry.TABLE_NAME;

    public static final String SQL_INSERT_ENTRIES =
            "INSERT OR REPLACE INTO " + Entry.TABLE_NAME + " (" +
                    Entry.COLUMN_NAME_MESSAGE_NAME + ", " +
                    Entry.COLUMN_NAME_SENDER + ", " +
                    Entry.COLUMN_NAME_LATITUDE + ", " +
                    Entry.COLUMN_NAME_LONGTITUDE + ", " +
                    Entry.COLUMN_NAME_PICTURE + ", " +
                    Entry.COLUMN_NAME_DDAY + ", " +
                    ") VALUES ";

    public static abstract class Entry implements BaseColumns
    {
        public static final String TABLE_NAME = "project_table";
        public static final String COLUMN_NAME_MESSAGE_NAME = "message_name";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGTITUDE = "longtitude";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_DDAY = "dday";
    }
}
