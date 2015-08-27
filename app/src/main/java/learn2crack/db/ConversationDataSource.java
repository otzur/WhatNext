package learn2crack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import learn2crack.models.WnConversation;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class ConversationDataSource {
    private static final String TAG = "WN";
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private String[] allColumns = { DBHelper.KEY_ROWID, DBHelper.KEY_N_USERS,DBHelper.KEY_OPTIONS_TYPE ,
            DBHelper.KEY_CONVERSATION_TYPE};

    public ConversationDataSource(Context ctx) {

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

    public WnConversation insert(int n_users, int options_type, String type){
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBHelper.KEY_N_USERS, n_users);
        initialValues.put(DBHelper.KEY_OPTIONS_TYPE, options_type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TYPE, type);
        Log.i(TAG, "insert conversation into database");
        long insertId = db.insert(DBHelper.TABLE_CONVERSATION_NAME, null, initialValues);

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME, allColumns, DBHelper.KEY_ROWID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        WnConversation newConversation = cursorToConversation(cursor);
        cursor.close();
        return newConversation;
    }

    private WnConversation cursorToConversation(Cursor cursor) {
        WnConversation wnConversation = new WnConversation();
        wnConversation.setId(cursor.getLong(0));
        wnConversation.setN_users(cursor.getInt(1));
        wnConversation.setOptions_type(cursor.getInt(2));
        wnConversation.setType(cursor.getString(3));
        return wnConversation;
    }
}
