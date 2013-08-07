package com.abir.photolotto.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "photolotto";

	// private Context context;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DatabaseConstants.CREATE_TABLE_PHOTOLOTTO);
		db.execSQL(DatabaseConstants.CREATE_TABLE_EMAIL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_PHOTOLOTTO
				+ "");
		db.execSQL("DROP TABLE IF EXISTS "
				+ DatabaseConstants.CREATE_TABLE_EMAIL + "");

		onCreate(db);
	}

	public boolean executeUpdate(String query, String... args) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL(query, args);
		return true;
	}

	public ArrayList<HashMap<String, String>> executeQuery(String query,
			String... args) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, args);

		ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();

		while (cursor.moveToNext()) {
			HashMap<String, String> tableRecordsHashMap = new HashMap<String, String>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				tableRecordsHashMap.put(cursor.getColumnName(i),
						cursor.getString(i));
			}
			array.add(tableRecordsHashMap);
		}
		cursor.close();
		db.close();
		return array;
	}

	public void insertRecord(String tableName, ContentValues values) {

		SQLiteDatabase db = this.getWritableDatabase();
		// db.execSQL("DROP TABLE IF EXISTS " +
		// DatabaseConstants.TABLE_PHOTOLOTTO
		// + "");
		//
		// db.execSQL(DatabaseConstants.CREATE_TABLE_PHOTOLOTTO);
		db.insert(tableName, null, values);
		db.close();
	}

	public void updateRecord(String tableName, ContentValues values,
			String where) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(tableName, values, where, null);
		db.close();
	}

	public void deleteRecord(String tableName, String where) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tableName, where, null);
		db.close();

	}

	public float getLastWeight() {
		float weight = 0;

		return weight;
	}

	public void drop() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_PHOTOLOTTO
				+ "");

		db.execSQL(DatabaseConstants.CREATE_TABLE_PHOTOLOTTO);
	}

}