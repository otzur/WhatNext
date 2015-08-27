package learn2crack.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class ChatDataSource {
    private static final String TAG = "WN";
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

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
}

