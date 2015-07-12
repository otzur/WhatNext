package learn2crack.chat;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by otzur on 7/12/2015.
 */
class MessageDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "WN DBAdapter";
    private static final String DATABASE_NAME = "MessageDB";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "userMessages";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_FROM = "user";
    public static final String KEY_TO = "to_user";
    public static final String KEY_OPTION_SELECTED = "option_selected";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CREATION_DATE = "delivery_date";

    private static final String CREATE_TABLE = "create table userMessages ("
            + "_id integer primary key autoincrement, "
            + "message text not null, "
            + "user text not null, "
            + "to_user text not null, "
            + "option_selected text not null, "
            + "type text not null, "
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
