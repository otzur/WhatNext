package learn2crack.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by otzur on 7/1/2015.
 */
public class MessageDataSource {
    public static final String KEY_ID = "id";
//    public static final String KEY_ROWID = "_id";
//    public static final String KEY_MESSAGE = "message";
//    public static final String KEY_FROM = "user";
//    public static final String KEY_TO = "to_user";
//    public static final String KEY_OPTION_SELECTED = "option_selected";
//    public static final String KEY_TYPE = "type";
//    public static final String KEY_CREATION_DATE = "delivery_date";

    private static final String TAG = "WN";
//    private static final String TABLE_NAME = "userMessages";
//    private static final String CREATE_TABLE =
//            "create table userMessages (id INTEGER primary key autoincrement, name text not null);";
//
//    private static final String CREATE_TABLE = "create table userMessages ("
//            + "_id integer primary key autoincrement, "
//            + "message text not null, "
//            + "user text not null, "
//            + "to_user text not null, "
//            + "option_selected text not null, "
//            + "type text not null, "
//            + "delivery_date text not null);";

    private MessageDatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { DBHelper.KEY_ROWID,  DBHelper.KEY_MESSAGE, DBHelper.KEY_FROM ,DBHelper.KEY_TO, DBHelper.KEY_OPTION_SELECTED, DBHelper.KEY_CREATION_DATE };

    public MessageDataSource(Context ctx) {

        DBHelper = new MessageDatabaseHelper(ctx);
    }

    //---open SQLite DB---
    public void open() throws SQLException {


        db = DBHelper.getWritableDatabase();
        //DBHelper.onUpgrade(db, 0, 1);
    }

    //---close SQLite DB---
    public void close() {
        DBHelper.close();
    }

    //---insert data into SQLite DB---
//    public long insert(String name) {
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_NAME, name);
//
//        Log.w(TAG, "Going to insert into database");
//        Cursor cursor = getAllData();
//        int count = cursor.getCount();
//        return db.insert(TABLE_NAME, null, initialValues);
//    }

    public WnMessage insert(String message, String user , String to_user, String selectedOptions, String type) {
        //Log.i(TAG, "to_user = " + to_user);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBHelper.KEY_MESSAGE, "message");
        initialValues.put(DBHelper.KEY_FROM, user);
        initialValues.put(DBHelper.KEY_TO, to_user);
        initialValues.put(DBHelper.KEY_OPTION_SELECTED, selectedOptions);
        initialValues.put(DBHelper.KEY_TYPE, type);
        initialValues.put(DBHelper.KEY_CREATION_DATE, currentDateandTime);

        Log.i(TAG, "Going to insert into database");
//        Cursor cursor = getAllData();
//        int count = cursor.getCount();
//        Log.w(TAG, "Database size " + count);
        long insertId = db.insert(DBHelper.TABLE_NAME, null, initialValues);

        Cursor cursor = db.query(DBHelper.TABLE_NAME, allColumns, DBHelper.KEY_ROWID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        WnMessage newWnMessage = cursorToMessage(cursor);
        cursor.close();
        return newWnMessage;

    }

    //---Delete All Data from table in SQLite DB---
    public void deleteAll() {
        db.delete(DBHelper.TABLE_NAME, null, null);
    }

    //---Get All Contacts from table in SQLite DB---
    public Cursor getAllData() {
//        return db.query(DBHelper.TABLE_NAME, new String[] {DBHelper.KEY_FROM, DBHelper.KEY_OPTION_SELECTED, DBHelper.KEY_TYPE},
//               null, null, null, null, null);

        Cursor cursor = db.query(DBHelper.TABLE_NAME, allColumns, null, null, null, null, null);
        return cursor;
    }

    private WnMessage cursorToMessage(Cursor cursor) {
        WnMessage message = new WnMessage();
        //message.setId(cursor.getLong(0));
        message.setMessage(cursor.getString(0));
        message.setUser(cursor.getString(1));
        message.setTo(cursor.getString(2));
        message.setOption_selected(cursor.getString(3));
        message.setDelivery_date(cursor.getString(4));
        return message;
    }

    public List<WnMessage> getAllMessages() {
        List<WnMessage> messages = new ArrayList<WnMessage>();

        Cursor cursor = db.query(DBHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WnMessage message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }
}
