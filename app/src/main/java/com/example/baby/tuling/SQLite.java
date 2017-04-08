package com.example.baby.tuling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by baby on 2017/4/3.
 */

public class SQLite extends SQLiteOpenHelper {
    public static final String ChatLog = "create table chat_log("
            + "chat_log_left text,"
            + "chat_log_right text)";
    Context mContext;

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChatLog);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
