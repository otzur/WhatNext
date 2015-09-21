package learn2crack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import learn2crack.models.WnChatMessage;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class ChatDataSource {
    private static final String TAG = "WN";
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private String[] allColumns = { DBHelper.KEY_ROWID, DBHelper.KEY_CHAT_DELIVERY,  DBHelper.KEY_CHAT_CONVERSATION_ID, DBHelper.KEY_CHAT_TEXT
            ,DBHelper.KEY_CHAT_FROM};

    public ChatDataSource(Context ctx) {

        DBHelper = DatabaseHelper.getInstance(ctx);
    }

    //---open SQLite DB---
    public void open() throws SQLException {


        db = DBHelper.getWritableDatabase();
        //DBHelper.onUpgrade(db, 0, 1);
    }

    public void close() {
        DBHelper.close();
    }

    public WnChatMessage insert(String text, String user, long conversationId){
        WnChatMessage message = new WnChatMessage();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDatedTime = sdf.format(new Date());
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBHelper.KEY_CHAT_DELIVERY, currentDatedTime);
        initialValues.put(DBHelper.KEY_CHAT_CONVERSATION_ID, conversationId);
        initialValues.put(DBHelper.KEY_CHAT_TEXT, text);
        initialValues.put(DBHelper.KEY_CHAT_FROM, user);
        long insertId = db.insert(DBHelper.TABLE_CHAT_NAME, null, initialValues);

        Cursor cursor = db.query(DBHelper.TABLE_CHAT_NAME, allColumns, DBHelper.KEY_ROWID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        message = cursorToChatMessage(cursor);
        cursor.close();
        return message;
    }

    private WnChatMessage cursorToChatMessage(Cursor cursor) {
        WnChatMessage message = new WnChatMessage();
        message.setId(cursor.getLong(0));
        message.setDelivery_date(cursor.getString(1));
        message.setConversation_id(cursor.getLong(2));
        message.setChat_text(cursor.getString(3));
        message.setFrom(cursor.getString(4));
        return message;
    }

    public Cursor getAllDataByConversationRowID(String rowId) {
        Cursor cursor = db.query(DBHelper.TABLE_CHAT_NAME, allColumns, DBHelper.KEY_CHAT_CONVERSATION_ID +"=?", new String[]{rowId}, null, null, null);
        return cursor;
    }

}

