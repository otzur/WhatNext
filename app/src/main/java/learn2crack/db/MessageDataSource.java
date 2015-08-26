package learn2crack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageResult;

import static learn2crack.utilities.Utils.getSelectedOpetions;

/**
 * Created by otzur on 7/1/2015.
 */
public class MessageDataSource {

    private static final String TAG = "WN";
    private MessageDatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private String[] allColumns = { DBHelper.KEY_ROWID, DBHelper.KEY_MESSAGE_ID,  DBHelper.KEY_MESSAGE, DBHelper.KEY_FROM
            ,DBHelper.KEY_TO, DBHelper.KEY_OPTION_SELECTED, DBHelper.KEY_TYPE, DBHelper.KEY_STATUS,  DBHelper.KEY_CREATION_DATE
            ,DBHelper.KEY_FILLED_BY_YOU,DBHelper.KEY_ASSOCIATED_TO_MESSAGE_ID};

    private String[] resultColumns = { DBHelper.KEY_MESSAGE_ID, DBHelper.KEY_OPTION_SELECTED, DBHelper.KEY_TYPE, DBHelper.KEY_ASSOCIATED_TO_MESSAGE_ID};

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

    public WnMessage insert(String message_id , String message, String user , String to_user, String selectedOptions, String type, String status,
                int filled_by_you, String associated_to_message_id) {
        //Log.i(TAG, "to_user = " + to_user);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDatedTime = sdf.format(new Date());

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBHelper.KEY_MESSAGE_ID, message_id);
        initialValues.put(DBHelper.KEY_MESSAGE, message);
        initialValues.put(DBHelper.KEY_FROM, user);
        initialValues.put(DBHelper.KEY_TO, to_user);
        initialValues.put(DBHelper.KEY_OPTION_SELECTED, selectedOptions);
        initialValues.put(DBHelper.KEY_TYPE, type);
        initialValues.put(DBHelper.KEY_STATUS, status);
        initialValues.put(DBHelper.KEY_CREATION_DATE, currentDatedTime);
        initialValues.put(DBHelper.KEY_FILLED_BY_YOU, filled_by_you);
        initialValues.put(DBHelper.KEY_ASSOCIATED_TO_MESSAGE_ID, associated_to_message_id);

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

    public WnMessageResult getResultsForMessageID(String messageID){
        WnMessageResult result = new WnMessageResult();
        WnMessage message = getMessage(messageID);
        if(message == null){
            return  result;
        }
        String options =message.getOption_selected();
        ArrayList<Integer> selectedOptions = getSelectedOpetions(options);
        Log.i("WN", "selected first: " + options);
        message = getMessage (message.getAssociated_to_message_id());
        options = message.getOption_selected();
        ArrayList<Integer> selectedOptions2 = getSelectedOpetions(options);
        ArrayList<String> matched=new ArrayList<>();
        for(int i = 0 ; i < selectedOptions.size() ; i++){
            if(selectedOptions2.contains(selectedOptions.get(i))){
                result.addMatched(""+selectedOptions.get(i));
            }
        }
        Log.i("WN","selected first: " + options);
        return result;
    }

    private WnMessage cursorToMessage(Cursor cursor) {
        WnMessage message = new WnMessage();
        message.setId(cursor.getLong(0));
        message.setMessage_id(cursor.getString(1));
        message.setMessage(cursor.getString(2));
        message.setUser(cursor.getString(3));
        message.setTo(cursor.getString(4));
        message.setOption_selected(cursor.getString(5));
        message.setType(cursor.getString(6));
        message.setStatus(cursor.getString(7));
        message.setDelivery_date(cursor.getString(8));
        message.setFilled_by_you(cursor.getInt(9));
        message.setAssociated_to_message_id(cursor.getString(10));
        return message;
    }

    public List<WnMessage> getAllMessages() {
        List<WnMessage> messages = new ArrayList<>();

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

    public WnMessage getMessage(String message_id) {
        WnMessage message = null;

        Cursor cursor = db.query(DBHelper.TABLE_NAME,
                allColumns, DBHelper.KEY_MESSAGE_ID +"=?", new String[]{message_id}, null, null, null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            message = cursorToMessage(cursor);
        }
        // make sure to close the cursor
        cursor.close();
        return message;
    }
}
