package learn2crack.chat;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samzaleg on 5/25/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

   // private static void addContact(Account account, String name, String username) {
        //Log.i(TAG, "Adding contact: " + name);
        //ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        //Create our RawContact
       /* ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
        builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(RawContacts.SYNC1, username);
        operationList.add(builder.build());

        //Create a Data record of common type 'StructuredName' for our RawContact
        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        operationList.add(builder.build());
*/
        //Create a Data record of custom type "vnd.android.cursor.item/vnd.fm.last.android.profile" to display a link to the Last.fm profile
     /*   ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.fm.last.android.profile");
        builder.withValue(ContactsContract.Data.DATA1, username);
        builder.withValue(ContactsContract.Data.DATA2, "Last.fm Profile");
        builder.withValue(ContactsContract.Data.DATA3, "View profile");
        operationList.add(builder.build());

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong during creation! " + e);
            e.printStackTrace();
        }
    }
*/

    private Uri addCallerIsSyncAdapterParameter(Uri uri){
        return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    private static String MIMETYPE = "wnMimeType";
    private void addWNContact(String name, String phone){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();
        int backId = 0;
        ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(RawContacts.CONTENT_URI))
                .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account")
                .withValue(RawContacts.ACCOUNT_NAME, "Account")
                        //.withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                        //.withValue(Settings.UNGROUPED_VISIBLE, true)
                .build());
        ops.add(ContentProviderOperation.newInsert(
                addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
       /* ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI))
                .withValueBackReference(RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(RawContacts.Data.MIMETYPE, MIMETYPE)
                .withValue(RawContacts.Data.DATA1, "fdfdfd")
                .withValue(RawContacts.Data.DATA2, "sample")
                .withValue(RawContacts.Data.DATA3, "sample")
                .build());*/

        /*ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                //.withValue(ContactsContract.Data.DATA1, "")//TODO:fill with gcd num
                .build());*/
        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e){
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String s, ContentProviderClient provider, SyncResult syncResult) {
        JSONParser json = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONArray res;
        ArrayList<String> mContacts = new ArrayList<String>();
        try {
            //String[] phones = new String[]{};
            ArrayList<String> phones = new ArrayList<String>();
            String MainNumber;
            Cursor peopleTemp;
            Cursor test = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            while (test != null && test.moveToNext()) {
                String id = test.getString(test.getColumnIndex(ContactsContract.Contacts._ID));
                String name = test.getString(test.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, };
                peopleTemp = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, RawContacts.CONTACT_ID + "= ?",
                        new String[]{id}, null);
                int indexNumber = peopleTemp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (peopleTemp.moveToNext()) {
                    MainNumber = peopleTemp.getString(indexNumber).replaceAll("[^0-9]", "");

                    phones.add(MainNumber);
                }
                JSONArray jsonArray = new JSONArray(Arrays.asList(phones));
                if(jsonArray.length()>0) {
                    mContacts.add("{\"name\": \"" + name + "\", \"phones\": " + jsonArray.get(0) + "}");
                    phones.clear();
                }
            }
            String valueArray = "[";
            for(int i=0; i > mContacts.size(); i++)
            {
                valueArray +=mContacts.get(i);
            }
            params.add(new BasicNameValuePair("contacts",mContacts.toString()));
            res = json.getJSONArray("http://nodejs-whatnext.rhcloud.com/synccontacts", params);
            params.clear();

            // getting all useres with wn accounts
            String[] projection    = new String[] {
                    RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER , RawContacts.ACCOUNT_TYPE};
            Cursor people = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, RawContacts.ACCOUNT_TYPE + "=?",
                    new String[]{ "learn2crack.chat.account" }, null);
            HashMap<String, String> syncMap = new HashMap<String,String>();
            for(int i = 0; i < res.length(); i++){
                String phone = res.getJSONObject(i).getString("phone");
                String name = res.getJSONObject(i).getString("name");
                syncMap.put(phone,name);
            }
            // check if current contacts are in res (if not should remove them)
            int indexRawID = people.getColumnIndex(RawContacts._ID);
            int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int indexName = people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            HashMap<String, String> wnContactsMap = new HashMap<String,String>();
            while (people.moveToNext()){
                MainNumber = people.getString(indexNumber).replaceAll("[^0-9]", "");;
                if(!syncMap.containsKey(MainNumber)){
                    String id = people.getString(indexRawID);
                    int deletedRawContacts = mContentResolver.delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build(), ContactsContract.RawContacts._ID + " = ?", new String[]{id});
                }
                else{
                    wnContactsMap.put(MainNumber,people.getString(indexName));
                }
            }
            //adds new wn contacts
            for (Map.Entry<String,String> contact : syncMap.entrySet()) {
                String phone = contact.getKey();
                if(!wnContactsMap.containsKey(phone)){
                    addWNContact(contact.getValue(),phone);
                }
            }
        }
        catch (Exception e){

        }
        /*Cursor cur = null;
        JSONObject allContacts = new JSONObject();
        JSONObject holder = null;
        try {
            cur = provider.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String id = null, name = null;
        Cursor pCur = null;
        String MainNumber="";
        boolean toAdd;
        while (cur.moveToNext()) {
            holder = new JSONObject();
            id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            try {
                holder.put("id",id);
                //holder.put("name",name);
                pCur = provider.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[]{id}, null);
                String[] projection    = new String[] {
                        RawContacts._ID, RawContacts.DISPLAY_NAME_PRIMARY,  ContactsContract.CommonDataKinds.Phone.CONTACT_ID,      ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor people = mContentResolver.query(  ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,   RawContacts.CONTACT_ID + "= ?",
                        new String[] { id }, null);
                //map.put("name", name);
                toAdd = false;
                while (pCur!= null && pCur.moveToNext()) {
                    String phonetype = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    MainNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    holder.put("phone", MainNumber);
                    if ((!name.equals("")) && (!MainNumber.equals(""))) {
                        toAdd = true;
                    }
                }
                if(toAdd){
                    allContacts.put("contact" ,holder);
                }
            }
            catch (Exception e){
            }
            //TODO: send contacts to sever via rest


            //TODO: add foreach contact we should update
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();
                int backId = 0;
                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                        .withValue(RawContacts.ACCOUNT_NAME, "Account")
                        .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account")
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                        .build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MainNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.Data.DATA1, 12345)
                        .withValue(ContactsContract.Data.DATA2, "data2")
                        .withValue(ContactsContract.Data.DATA3, "data3")
                        .build());
                try {
                    provider.applyBatch(ops);
                }
                catch (Exception e){
                }
            }
        if(pCur != null) {
            pCur.close();
        }*/
    }

   /* private static void performSync(Context context, Account account, Bundle extras) {
        HashMap<String, Long> localContacts = new HashMap<String, Long>();
        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                RawContacts.ACCOUNT_TYPE, account.type).build();
        Cursor c1 = context.getContentResolver().query(rawContactUri, new String[]{BaseColumns._ID, RawContacts.SYNC1}, null, null, null);
        while (c1.moveToNext()) {
            localContacts.put(c1.getString(1), c1.getLong(0));
        }
    }*/
}
