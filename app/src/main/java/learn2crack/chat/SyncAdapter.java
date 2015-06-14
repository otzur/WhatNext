package learn2crack.chat;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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

   // private static String MIMETYPE = "wnMimeType";

    private void addWNContact(String name, String phone){
        List<NameValuePair> paramsDebug = new ArrayList<NameValuePair>();
        JSONParser json = new JSONParser();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        //int rawContactInsertIndex = ops.size();
        int backId = 0;
        try {
        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_NAME, "Account")
                .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account")
                .build());//.withValue(ContactsContract.Settings.UNGROUPED_VISIBLE, true)
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        /*ContentValues values = new ContentValues();
        values.put(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account");
        values.put(RawContacts.ACCOUNT_NAME, "Account");
        Uri rawContactUri = mContentResolver.insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
       mContentResolver.insert(ContactsContract.Data.CONTENT_URI, values);*/

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

             ContentProviderResult[] res = mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            for(int i=0; i<res.length; i++){
                paramsDebug.add(new BasicNameValuePair("msg", res[i].toString() + res[i].describeContents()));
                JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                paramsDebug.clear();
            }
        }
        catch (Exception e){
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            paramsDebug.add(new BasicNameValuePair("msg", e.toString() + "\n stack trace#####:\n "+ writer.toString()));
            JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            paramsDebug.clear();
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String s, ContentProviderClient provider, SyncResult syncResult) {
        JSONParser json = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        List<NameValuePair> paramsDebug = new ArrayList<NameValuePair>();
        String getres="";
        JSONArray res;
        Cursor test = null;
        ArrayList<String> phones = new ArrayList<String>();
        String MainNumber;
        Cursor peopleTemp = null;
        Cursor people = null;
        String id="",name="";
        ArrayList<JSONObject> mContacts = new ArrayList<JSONObject>();
        try {
            //String[] phones = new String[]{};

            paramsDebug.add(new BasicNameValuePair("msg","before cursor"));
            JSONArray debug1=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            paramsDebug.clear();
            test = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            paramsDebug.add(new BasicNameValuePair("msg","after cursor"));
            JSONArray debug2=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            paramsDebug.clear();
            while (test != null && test.moveToNext()) {
                try {
                    id = test.getString(test.getColumnIndex(ContactsContract.Contacts._ID));
                    name = test.getString(test.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
                catch (Exception ex){
                    paramsDebug.add(new BasicNameValuePair("msg", ex.toString() + "\n stack trace#####:\n " + ex.getStackTrace().toString()));
                    JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                    paramsDebug.clear();
                    continue;
                }
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, };
                if(id == null || id.equals("") || name == null || name.equals("")){
                    paramsDebug.add(new BasicNameValuePair("msg","id or name problem"));
                    JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                    paramsDebug.clear();
                    continue;
                }
                peopleTemp = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, RawContacts.CONTACT_ID + "= ?",
                        new String[]{id}, null);
                if(peopleTemp == null){
                    continue;
                }
                int indexNumber = peopleTemp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (peopleTemp.moveToNext()) {
                    MainNumber = peopleTemp.getString(indexNumber).replaceAll("[^0-9]", "");
                    if(!MainNumber.equals("")) {
                        phones.add(MainNumber);
                    }
                }
                JSONArray jsonArray = new JSONArray(phones);

                if(jsonArray!= null && jsonArray.length()>0 && phones.size()>0 && (!name.equals(""))) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("name", name);
                    jsonObj.put("phones",jsonArray);
                    //mContacts.add("{\"name\": \"" + name + "\", \"phones\": " + jsonArray.get(0) + "}");
                    mContacts.add(jsonObj);
                    phones.clear();
                }

                if(peopleTemp!=null){
                    peopleTemp.close();
                }
            }
            if(test!=null){
                test.close();
            }

            paramsDebug.add(new BasicNameValuePair("msg", "size is: "+mContacts.size()));
            JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            paramsDebug.clear();
            params.add(new BasicNameValuePair("contacts",mContacts.toString()));
            res = json.getJSONArray("http://nodejs-whatnext.rhcloud.com/synccontacts", params);
            params.clear();

            // getting all useres with wn accounts
            String[] projection    = new String[] {
                    RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER , RawContacts.ACCOUNT_TYPE};
            people = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, RawContacts.ACCOUNT_TYPE + "=?",
                    new String[]{ "learn2crack.chat.account" }, null);
            HashMap<String, String> syncMap = new HashMap<String,String>();
            for(int i = 0; i < res.length(); i++){
                String phone = res.getJSONObject(i).getString("phone");
                name = res.getJSONObject(i).getString("name");
                if(!phone.equals("")) {
                    syncMap.put(phone, name);
                }
            }
            // check if current contacts are in res (if not should remove them)

            HashMap<String, String> wnContactsMap = new HashMap<String,String>();
            if(people!=null) {
                int indexRawID = people.getColumnIndex(RawContacts._ID);
                int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int indexName = people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                while (people.moveToNext()) {
                    MainNumber = people.getString(indexNumber).replaceAll("[^0-9]", "");
                    if (!syncMap.containsKey(MainNumber)) {
                        paramsDebug.add(new BasicNameValuePair("msg", "delete account for name: "+people.getString(indexNumber)));
                        JSONArray debug4=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                        paramsDebug.clear();
                        id = people.getString(indexRawID);
                        int deletedRawContacts = mContentResolver.delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build(), ContactsContract.RawContacts._ID + " = ?", new String[]{id});
                    } else {
                        wnContactsMap.put(MainNumber, people.getString(indexName));
                    }
                }
            }
            if(people != null){
                people.close();
            }
            //adds new wn contacts
            for (Map.Entry<String,String> contact : syncMap.entrySet()) {
                String phone = contact.getKey();
                if(!wnContactsMap.containsKey(phone)){
                    //paramsDebug.add(new BasicNameValuePair("msg", "add wn contact with number: " + phone + " and name: " + contact.getValue()));
                    //JSONArray debug3=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                    //paramsDebug.clear();
                    String newWNPhone = contact.getKey();
                    //boolean debug = true;
                    for(int i=0;i<mContacts.size();i++) {
                        JSONObject json_obj = mContacts.get(i);
                        JSONArray phonesTemp = json_obj.getJSONArray("phones");
                        for(int j=0; j<phonesTemp.length();j++) {
                            if (phonesTemp.getString(j).equals(newWNPhone)){
                                //paramsDebug.add(new BasicNameValuePair("msg", "found in current contact list- name: " + contact.getValue()));
                                //JSONArray debug4 = json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                                //paramsDebug.clear();
                                addWNContact(json_obj.getString("name"), phone);
                                //debug = false;
                                break;
                            }
                        }
                    }
                   /* if(debug){
                        paramsDebug.add(new BasicNameValuePair("msg", "NOT found in contact list- name: " + contact.getValue()));
                        JSONArray debug4=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                        paramsDebug.clear();
                    }*/
                    //addWNContact(contact.getValue(), phone);
                }
            }
        }
        catch (Exception e){
            if(test!=null){
                test.close();
            }
            if(peopleTemp!=null){
                peopleTemp.close();
            }
            if(people != null){
                people.close();
            }
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            paramsDebug.add(new BasicNameValuePair("msg", e.toString() + "\n stack trace#####:\n "+ writer.toString()));
            JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            paramsDebug.clear();
        }
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
