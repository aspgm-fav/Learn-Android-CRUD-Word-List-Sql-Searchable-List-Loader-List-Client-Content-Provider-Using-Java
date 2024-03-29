package com.example.list_sql_with_content_provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.list_sql_with_content_provider.Contract.ALL_ITEMS;
import static com.example.list_sql_with_content_provider.Contract.DATABASE_NAME;
import static com.example.list_sql_with_content_provider.Contract.WordList.KEY_ID;
import static com.example.list_sql_with_content_provider.Contract.WordList.KEY_WORD;
import static com.example.list_sql_with_content_provider.Contract.WordList.WORD_LIST_TABLE;

public class WordListOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = WordListOpenHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase mWritableDB;
    SQLiteDatabase mReadableDB;
    ContentValues mValues = new ContentValues();
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " + // will auto-increment if no value passed
                    KEY_WORD + " TEXT );";
    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);
    }
    public void fillDatabaseWithData(SQLiteDatabase db) {
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask", "Android Studio",
                "SQLiteDatabase", "SQLOpenHelper", "Data model", "ViewHolder",
                "Android Performance", "OnClickListener"};
        ContentValues values = new ContentValues();
        for (int i=0; i < words.length;i++) {
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WordListOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db);
    }
    public Cursor query(int position) {
        String query;
        if (position != ALL_ITEMS) {
            position++; // Because database starts counting at 1.
            query = "SELECT " + KEY_ID + "," + KEY_WORD + " FROM " + WORD_LIST_TABLE +
                    " WHERE " + KEY_ID + "=" + position + ";";
        } else {
            query = "SELECT  * FROM " + WORD_LIST_TABLE + " ORDER BY " + KEY_WORD + " ASC ";
        }

        Cursor cursor = null;
        try {
            if (mReadableDB == null) {
                mReadableDB = this.getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
        } catch (Exception e) {
            Log.d(TAG, "QUERY EXCEPTION! " + e);
        } finally {
            return cursor;
        }
    }
    public Cursor count() {
        MatrixCursor cursor = new MatrixCursor(new String[]{Contract.CONTENT_PATH});
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            int count = (int) DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
            cursor.addRow(new Object[]{count});
        } catch (Exception e) {
            Log.d(TAG, "COUNT EXCEPTION " + e);
        }
        return cursor;
    }
    public long insert(ContentValues values){
        long added = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            added = mWritableDB.insert(WORD_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION " + e);
        }
        return added;
    }
    public int update(int id, String word) {
        int updated = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            updated = mWritableDB.update(WORD_LIST_TABLE,
                    values,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d (TAG, "UPDATE EXCEPTION " + e);
        }
        return updated;
    }
    public int delete(int id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = this.getWritableDatabase();
            }
            deleted = mWritableDB.delete(WORD_LIST_TABLE,
                    KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d (TAG, "DELETE EXCEPTION " + e);
        }
        return deleted;
    }
}