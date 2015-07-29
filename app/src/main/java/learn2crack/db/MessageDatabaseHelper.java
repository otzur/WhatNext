package learn2crack.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by otzur on 7/12/2015.
 */
public class MessageDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "WN DBAdapter";
    private static final String DATABASE_NAME = "MessageDB";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "userMessages";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_FROM = "user";
    public static final String KEY_TO = "to_user";
    public static final String KEY_OPTION_SELECTED = "option_selected";
    public static final String KEY_TYPE = "type";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATION_DATE = "delivery_date";

    private static final String CREATE_TABLE = "create table userMessages ("
            + "_id integer primary key autoincrement, "
            + "message_id text not null, "
            + "message text not null, "
            + "user text not null, "
            + "to_user text not null, "
            + "option_selected text not null, "
            + "type text not null, "
            + "status text not null, "
            + "delivery_date text not null);";

    MessageDatabaseHelper(Context context) {
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
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS userMessages");
        onCreate(db);
    }
}
