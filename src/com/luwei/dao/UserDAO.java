package com.luwei.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UserDAO {
    SqliteBase sql;

    public UserDAO(Context context) {
        super();
        sql = new SqliteBase(context);
    }

    public boolean checkPreId(String preId) {
        SQLiteDatabase db = sql.getWritableDatabase();
        if (preId != null) {
            Cursor cs = db.rawQuery("select preId from user where preId = ? ", new String[]{preId});
            if (cs.moveToNext()) {
                return true;
            } else
                return false;
        } else
            return false;
    }

    public void insertUser() {

    }

}
