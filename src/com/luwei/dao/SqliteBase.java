package com.luwei.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteBase extends SQLiteOpenHelper {

	private static final String TAG = "TAG";

	public SqliteBase(Context context) {
		super(context, "xinxiang", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL("create table user(id integer primary key autoincrement,preId varchar(20),really_name varchar(20),nick_name varchar(20),email varchar(20),avatar_url varchar(40))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "更改数据库");
	}

}
