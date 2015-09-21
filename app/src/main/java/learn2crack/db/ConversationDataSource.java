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
import learn2crack.models.WnContact;
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

    private String[] allColumns = { DBHelper.KEY_ROWID, DBHelper.KEY_CONVERSATION_GUID, DBHelper.KEY_OPTIONS_TYPE ,
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

    public long update(String conversation_guid, int options_type, String type, String tab, String contact_phone,
                                 String contact_name, String status){


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String updateDatedTime = sdf.format(new Date());

        ContentValues initialValues = new ContentValues();
        //initialValues.put(DBHelper.KEY_CONVERSATION_GUID, conversation_id);
        initialValues.put(DBHelper.KEY_OPTIONS_TYPE, options_type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TYPE, type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TAB, tab);
        initialValues.put(DBHelper.KEY_CONTACT_PHONE, contact_phone);
        initialValues.put(DBHelper.KEY_CONTACT_NAME, contact_name);
        initialValues.put(DBHelper.KEY_CONVERSATION_STATUS, status);
        initialValues.put(DBHelper.KEY_CONVERSATION_DATE, updateDatedTime);

        Log.i(TAG, "update conversation into database");
        long insertId = db.update(DBHelper.TABLE_CONVERSATION_NAME, initialValues, DBHelper.KEY_CONVERSATION_GUID +"=?", new String[]{conversation_guid});

        Log.i(TAG, "conversation update status = " + status);
        Log.i(TAG, "conversation update ID = " + conversation_guid);
        return insertId;
    }


    public WnConversation insert(String conversation_guid, int options_type, String type, String tab,
                                 String contact_phone, String contact_name, String status){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String updateDatedTime = sdf.format(new Date());
        ContentValues initialValues = new ContentValues();

        initialValues.put(DBHelper.KEY_CONVERSATION_GUID, conversation_guid);
        //initialValues.put(DBHelper.KEY_N_USERS, n_users);
        initialValues.put(DBHelper.KEY_OPTIONS_TYPE, options_type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TYPE, type);
        initialValues.put(DBHelper.KEY_CONVERSATION_TAB, tab);
        initialValues.put(DBHelper.KEY_CONTACT_PHONE, contact_phone);
        initialValues.put(DBHelper.KEY_CONTACT_NAME, contact_name);
        initialValues.put(DBHelper.KEY_CONVERSATION_STATUS, status);
        initialValues.put(DBHelper.KEY_CONVERSATION_DATE, updateDatedTime);

        Log.i(TAG, "insert conversation into database");
        long rowId = db.insert(DBHelper.TABLE_CONVERSATION_NAME, null, initialValues);

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME, allColumns, DBHelper.KEY_ROWID + " = " + rowId, null, null, null, null);
        cursor.moveToFirst();
        WnConversation newConversation = cursorToConversation(cursor);
        cursor.close();

        Log.i(TAG, "conversation insert status = " + status);
        Log.i(TAG, "conversation insert ID = " + conversation_guid);
        return newConversation;
    }

    private WnConversation cursorToConversation(Cursor cursor) {
        WnConversation wnConversation = new WnConversation();
        wnConversation.setRowId(cursor.getLong(0));
        wnConversation.setConversation_guid(cursor.getString(1));
        //wnConversation.setN_users(cursor.getInt(2));
        wnConversation.setOptions_type(cursor.getInt(2));
        wnConversation.setType(cursor.getString(3));
        wnConversation.setTab(cursor.getInt(4));

        WnContact contact = new WnContact(cursor.getString(6), cursor.getString(5));
        wnConversation.addContact(contact);

//        wnConversation.setContact_phone_number(cursor.getString(5));
//        wnConversation.setUser_name(cursor.getString(6));
        wnConversation.setStatus(cursor.getString(7));
        wnConversation.setUpdate_datetime(cursor.getString(8));
        return wnConversation;
    }

    public List<WnConversation> getAllConversations() {
        List<WnConversation> conversations = new ArrayList<>();

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WnConversation conversation = cursorToConversation(cursor);
            conversation.getContacts().get(0).setPhoto(Contacts.retrieveContactPhoto(MainActivity.getAppContext(), conversation.getContacts().get(0).getPhoneNumber()));
            conversations.add(conversation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return conversations;
    }
    public WnConversation getConversationByGUID(String conversation_guid) {
        WnConversation conversation = null;

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME,
                allColumns, DBHelper.KEY_CONVERSATION_GUID +"=?", new String[]{conversation_guid}, null, null, null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            conversation = cursorToConversation(cursor);
        }
        // make sure to close the cursor
        cursor.close();
        return conversation;
    }

    public WnConversation getConversationByRowID(String rowId){
        WnConversation conversation = null;

        Cursor cursor = db.query(DBHelper.TABLE_CONVERSATION_NAME,
                allColumns, DBHelper.KEY_ROWID +"=?", new String[]{rowId}, null, null, null);

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
        ConversationDataSource conversationDataSource = new ConversationDataSource(context);
        conversationDataSource.open();
        Set<String> mSet = new HashSet<>();
        WnConversation wnConversation =  conversationDataSource.getConversationByRowID(Long.valueOf(conversationRowID).toString());
        mSet.add(wnConversation.getContacts().get(0).getPhoneNumber());
        return mSet;
//
//        messageDataSource.open();
//        Set<String> mSet = new HashSet<>();
//        ArrayList<WnMessage> messages= messageDataSource.getRelatedMessages(conversationRowID, null);
//        String tempUser, tempTo;
//        for(int i = 0; i < messages.size(); i++){
//            tempUser = messages.get(i).getUser();
//            if(tempUser != null) {
//                mSet.add(tempUser);
//            }
//            tempTo = messages.get(i).getUser();
//            if(tempTo != null) {
//                mSet.add(tempTo);
//            }
//        }
//        return mSet;
    }

    public WnMessageResult getResultsForConversation(String c_id){

        Log.i(TAG, "getResultsForConversation c_id=" + c_id);
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
