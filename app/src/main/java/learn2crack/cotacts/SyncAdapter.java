package learn2crack.cotacts;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learn2crack.utilities.JSONParser;

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


    private Uri addCallerIsSyncAdapterParameter(Uri uri){
        return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

   // private static String MIMETYPE = "wnMimeType";

    private void addWNContact(String name, String phone){
        List<NameValuePair> paramsDebug = new ArrayList<NameValuePair>();
        JSONParser json = new JSONParser();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
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
             ContentProviderResult[] res = mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            for(int i=0; i<res.length; i++){
                paramsDebug.add(new BasicNameValuePair("msg", res[i].toString()));
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
        JSONArray res;
        Cursor test = null;
        ArrayList<String> phones = new ArrayList<String>();
        String MainNumber;
        Cursor peopleTemp = null;
        Cursor people = null;
        String id="",name="";
        ArrayList<JSONObject> mContacts = new ArrayList<JSONObject>();
        HashMap<String, String> originalPhoneMap = new HashMap<String,String>();
        try {
            test = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            while (test != null && test.moveToNext()) {
                try {
                    id = test.getString(test.getColumnIndex(ContactsContract.Contacts._ID));
                    name = test.getString(test.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
                catch (Exception ex){
                    continue;
                }
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, };
                if(id == null || id.equals("") || name == null || name.equals("")){
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
                        originalPhoneMap.put(MainNumber,peopleTemp.getString(indexNumber));
                    }
                }
                JSONArray jsonArray = new JSONArray(phones);

                if(jsonArray!= null && jsonArray.length()>0 && phones.size()>0 && (!name.equals(""))) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("name", name);
                    jsonObj.put("phones",jsonArray);
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
            //paramsDebug.add(new BasicNameValuePair("msg", "size is: "+mContacts.size()));
            //JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            //paramsDebug.clear();
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
                        //paramsDebug.add(new BasicNameValuePair("msg", "delete account for name: "+people.getString(indexNumber)));
                        //JSONArray debug4=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
                        //paramsDebug.clear();
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
                    String newWNPhone = contact.getKey();
                    for(int i=0;i<mContacts.size();i++) {
                        JSONObject json_obj = mContacts.get(i);
                        JSONArray phonesTemp = json_obj.getJSONArray("phones");
                        for(int j=0; j<phonesTemp.length();j++) {
                            if (phonesTemp.getString(j).equals(newWNPhone)){
                                addWNContact(json_obj.getString("name"), originalPhoneMap.get(phone));
                                break;
                            }
                        }
                    }
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
            //paramsDebug.add(new BasicNameValuePair("msg", e.toString() + "\n stack trace#####:\n "+ writer.toString()));
            //JSONArray debug6=json.getJSONArray("http://nodejs-whatnext.rhcloud.com/log", paramsDebug);
            //paramsDebug.clear();
        }
    }

}
