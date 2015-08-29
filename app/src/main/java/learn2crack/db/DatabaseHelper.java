package learn2crack.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by otzur on 7/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "WN DBAdapter";
    private static final String DATABASE_NAME = "MessageDB";
    private static final int DATABASE_VERSION = 2;

    public static final String KEY_ROWID = "_id";
    /*
    * user messages consts
    */
    public static final String TABLE_MESSAGES_NAME = "userMessages";
    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_FROM = "user";
    public static final String KEY_TO = "to_user";
    public static final String KEY_OPTION_SELECTED = "option_selected";
    public static final String KEY_TYPE = "type";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATION_DATE = "delivery_date";
    public static final String KEY_FILLED_BY_YOU = "filled_by_current_user";
    public static final String KEY_CONVERSATION_ROW_ID = "c_id";
    /*
    * conversation consts
    */
    public static final String TABLE_CONVERSATION_NAME = "conversations";
    public static final String KEY_CONVERSATION_ID = "conversation_id";
    public static final String KEY_N_USERS = "n_users";
    public static final String KEY_OPTIONS_TYPE ="options_type";
    public static final String KEY_CONVERSATION_TYPE = "type";
    public static final String KEY_CONVERSATION_TAB = "tab";


    /*
    * tables creation consts
    */
    private static final String CREATE_TABLE = "create table userMessages ("
            + "_id integer primary key autoincrement, "
            + "message_id text not null, "
            + "message text not null, "
            + "user text not null, "
            + "to_user text not null, "
            + "option_selected text not null, "
            + "type text not null, "
            + "status text not null, "
            + "delivery_date text not null, "
            + "filled_by_current_user integer not null, "
            +"c_id integer not null, "
            +"FOREIGN KEY(c_id) REFERENCES conversations(_id));";


    private static final String CREATE_TABLE_CONVERSATIONS ="create table conversations ("
            + "_id integer primary key autoincrement, "
            + "conversation_id text not null, "
            + "type text not null, "
            + "n_users integer not null, "
            + "options_type integer not null, "
            + "tab integer not null);";

    private static final String CREATE_TABLE_OPTIONS_DEFAULTED ="create table defaultedOptions ("
            +"_id integer primary key autoincrement, "
            + "options_type integer not null, "
            + "option_index integer not null, "
            + "o_text text not null);";

    private static final String CREATE_TABLE_CHAT ="create table chat ("
    +"_id integer primary key autoincrement, "
    +"delivery_date text not null, "
    +"c_id integer not null, "
    +"chat_text text not null, "
    +"FOREIGN KEY(c_id) REFERENCES conversations(_id));";

   /* private static final String CREATE_TABLE_2 = "create table messageResponds ("
            + "_id integer primary key autoincrement, "
            + "message_id text not null , "
            + "message text not null, "
            + "user text not null, "
            + "to_user text not null, "
            + "option_selected text not null, "
            + "type text not null, "
            + "status text not null, "
            + "delivery_date text not null,"
            + "FOREIGN KEY(message_id) REFERENCES checklist(message_id));";*/

    private static DatabaseHelper mDataBaseHelper = null;
    private static Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDataBaseHelper.context = context;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (mDataBaseHelper == null) {
            mDataBaseHelper = new DatabaseHelper(context);
        }
        return mDataBaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.i(TAG, "Create DB tables");
            db.execSQL(CREATE_TABLE_CONVERSATIONS);
            Log.i(TAG, "table conversations created");
            db.execSQL(CREATE_TABLE);
            Log.i(TAG, "table userMessages created");
            db.execSQL(CREATE_TABLE_OPTIONS_DEFAULTED);
            Log.i(TAG, "table defaultedOptions created");
            db.execSQL(CREATE_TABLE_CHAT);
            Log.i(TAG, "table chat created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS userMessages");
        db.execSQL("DROP TABLE IF EXISTS defaultedOptions");
        db.execSQL("DROP TABLE IF EXISTS chat");
        db.execSQL("DROP TABLE IF EXISTS conversations");
        onCreate(db);
    }
}
