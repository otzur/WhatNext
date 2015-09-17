package learn2crack.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import learn2crack.activities.MainActivity;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageResult;
import learn2crack.utilities.Contacts;

import static learn2crack.utilities.Utils.getSelectedOpetions;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class ConversationDataSource {
    private static final String TAG = "WN";
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private Context context;

    private String[] allColumns = { DBHelper.KEY_ROWID, DBHelper.KEY_CONVERSATION_ID, DBHelper.KEY_N_USERS,DBHelper.KEY_OPTIONS_TYPE ,
            DBHelper.KEY_CONVERSATION_TYPE, DBHelper.KEY_CONVERSATION_TAB, DBHelper.KEY_CONTACT_PHONE, DBHelper.KEY_CONTACT_NAME,
            DBHelper.KEY_STATUS, DBHelper.KEY_CONVERSATION_DATE};

    public ConversationDataSource(Context ctx) {
        this.context = ctx;
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

    public void update(String conversation_id, int n_users, int options_type, String type, String tab, String contact_phone,
                                 String contact_name, String status){


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String updateDatedTime = sdf.format(new Date());

        ContentValues initialValues = new ContentValues();
        //initialValues.put(DBHelper.KEY_CONVERSATION_ID, conversation_id);
        initialValues.put(DBHelper.KEY_N_USERS, n_users);
        initialValues.put(DBHelper.KEY_OPTIONS_TYPE, options_type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TYPE, type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TAB, tab);
        initialValues.put(DBHelper.KEY_CONTACT_PHONE, contact_phone);
        initialValues.put(DBHelper.KEY_CONTACT_NAME, contact_name);
        initialValues.put(DBHelper.KEY_CONVERSATION_STATUS, status);
        initialValues.put(DBHelper.KEY_CONVERSATION_DATE, updateDatedTime);

        Log.i(TAG, "update conversation into database");
        long insertId = db.update(DBHelper.TABLE_CONVERSATION_NAME, initialValues, DBHelper.KEY_CONVERSATION_ID +"=?", new String[]{conversation_id});

        Log.i(TAG, "conversation update status = " + status);
        Log.i(TAG, "conversation update ID = " + conversation_id);
        return;
    }


    public WnConversation insert(String conversation_id, int n_users, int options_type, String type, String tab, String contact_phone,
                                 String contact_name, String status){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String updateDatedTime = sdf.format(new Date());

        ContentValues initialValues = new ContentValues();
        initialValues.put(DBHelper.KEY_CONVERSATION_ID, conversation_id);
        initialValues.put(DBHelper.KEY_N_USERS, n_users);
        initialValues.put(DBHelper.KEY_OPTIONS_TYPE, options_type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TYPE, type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TAB, tab);
        initialValues.put(DBHelper.KEY_CONTACT_PHONE, contact_phone);
        initialValues.put(DBHelper.KEY_CONTACT_NAME, contact_name);
        initialValues.put(DBHelper.KEY_CONVERSATION_STATUS, status);
        initialValues.put(DBHelper.KEY_CONVERSATION_DATE, updateDatedTime);
        Log.i(TAG, "insert conversation into database");
        long insertId = db.insert(DBHelper.TABLE_CONVERSATION_NAME, null, initialValues);

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME, allColumns, DBHelper.KEY_ROWID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        WnConversation newConversation = cursorToConversation(cursor);
        cursor.close();

        Log.i(TAG, "conversation insert status = " + status);
        Log.i(TAG, "conversation insert ID = " + conversation_id);
        return newConversation;
    }

    private WnConversation cursorToConversation(Cursor cursor) {
        WnConversation wnConversation = new WnConversation();
        wnConversation.setId(cursor.getLong(0));
        wnConversation.setConversation_id(cursor.getString(1));
        wnConversation.setN_users(cursor.getInt(2));
        wnConversation.setOptions_type(cursor.getInt(3));
        wnConversation.setType(cursor.getString(4));
        wnConversation.setTab(cursor.getInt(5));
        wnConversation.setContact_phone_number(cursor.getString(6));
        wnConversation.setUser_name(cursor.getString(7));
        wnConversation.setStatus(cursor.getString(8));
        wnConversation.setUpdate_datetime(cursor.getString(9));
        return wnConversation;
    }

    public List<WnConversation> getAllConversations() {
        List<WnConversation> conversations = new ArrayList<>();

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WnConversation conversation = cursorToConversation(cursor);
            conversation.setUser_photo(Contacts.retrieveContactPhoto(MainActivity.getAppContext(), conversation.getContact_phone_number()));
            conversations.add(conversation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return conversations;
    }
    public WnConversation getConversationByUniqeID(String conversation_id) {
        WnConversation conversation = null;

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME,
                allColumns, DBHelper.KEY_CONVERSATION_ID +"=?", new String[]{conversation_id}, null, null, null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            conversation = cursorToConversation(cursor);
        }
        // make sure to close the cursor
        cursor.close();
        return conversation;
    }

    public WnConversation getConversationByID(String c_id){
        WnConversation conversation = null;

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME,
                allColumns, DBHelper.KEY_ROWID +"=?", new String[]{c_id}, null, null, null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            conversation = cursorToConversation(cursor);
        }
        // make sure to close the cursor
        cursor.close();
        return conversation;
    }

    public Set getUsersPhones(Long conversationRowID){
        MessageDataSource messageDataSource = new MessageDataSource(context);
        messageDataSource.open();
        Set<String> mSet = new HashSet<>();
        ArrayList<WnMessage> messages= messageDataSource.getRelatedMessages(conversationRowID, null);
        String tempUser, tempTo;
        for(int i = 0; i < messages.size(); i++){
            tempUser = messages.get(i).getUser();
            if(tempUser != null) {
                mSet.add(tempUser);
            }
            tempTo = messages.get(i).getUser();
            if(tempTo != null) {
                mSet.add(tempTo);
            }
        }
        return mSet;
    }

    public WnMessageResult getResultsForConversation(String c_id){
        WnMessageResult result = new WnMessageResult();
        MessageDataSource messageDataSource = new MessageDataSource(context);
        messageDataSource.open();
        ArrayList<WnMessage> relatedMessages = messageDataSource.getRelatedMessages(Long.valueOf(c_id) , null);
            String options =relatedMessages.get(0).getOption_selected();
        ArrayList<Integer> selectedOptions = getSelectedOpetions(options);
        messageDataSource.close();
        int relatedMessageCount = relatedMessages.size();
        for(int k=1 ; k < relatedMessageCount ; k++) {
            selectedOptions.retainAll(getSelectedOpetions(
                    relatedMessages.get(k).getOption_selected()));
        }
        if(relatedMessageCount >= 2) {
            for (int i = 0; i < selectedOptions.size(); i++) {
                result.addMatched("" + selectedOptions.get(i));
            }
        }
        if(relatedMessageCount == 1){
            result.setAllUsersResponded(false);
        }
        else {
            result.setAllUsersResponded(true);
        }
        return result;
    }
}
