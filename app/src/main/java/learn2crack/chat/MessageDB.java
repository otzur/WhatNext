package learn2crack.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by otzur on 7/1/2015.
 */
public class MessageDB {
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    private static final String TAG = "WN DBAdapter";
    private static final String DATABASE_NAME = "MessageDB";

    private static final String TABLE_NAME = "userMessages";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "create table userMessages (id INTEGER primary key autoincrement, name text not null);";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public MessageDB(Context ctx) {

        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {

                db.execSQL(CREATE_TABLE);
                Log.i(TAG, "Create DB table");
                Log.i(TAG, CREATE_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS userMessages");
            onCreate(db);
        }
    }

    //---open SQLite DB---
    public MessageDB open() throws SQLException {


        db = DBHelper.getWritableDatabase();
        //DBHelper.onUpgrade(db, 0, 1);
        return this;
    }

    //---close SQLite DB---
    public void close() {
        DBHelper.close();
    }

    //---insert data into SQLite DB---
    public long insert(String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);

        Log.w(TAG, "Going to insert into database");
        Cursor cursor = getAllData();
        int count = cursor.getCount();
        return db.insert(TABLE_NAME, null, initialValues);
    }

    //---Delete All Data from table in SQLite DB---
    public void deleteAll() {
        db.delete(TABLE_NAME, null, null);
    }

    //---Get All Contacts from table in SQLite DB---
    public Cursor getAllData() {
        return db.query(TABLE_NAME, new String[] {KEY_NAME},
                null, null, null, null, null);
    }
}
