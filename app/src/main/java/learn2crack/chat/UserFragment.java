package learn2crack.chat;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.provider.ContactsContract;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.provider.ContactsContract.*;


public class UserFragment extends Fragment {
    ListView list;
    ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    Button refresh,logout;
    List<NameValuePair> params;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.user_fragment, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);

        list = (ListView)view.findViewById(R.id.listView);
        refresh = (Button)view.findViewById(R.id.refresh);
        logout = (Button)view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  Logout().execute();

            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.content_frame)).commit();
                Fragment reg = new UserFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        try {
            new Load().execute();
        }
        catch(Exception e){
            int sds =23;
        }
        return view;
    }

    private class Load extends AsyncTask<String, String, ArrayList<HashMap<String, String> > > {
        /*public JSONArray arrayLToJSON(ArrayList<HashMap<String, String>> list)
        {
            JSONArray json_arr=new JSONArray();
            for (HashMap<String, String> map : list) {
                JSONObject json_obj=new JSONObject();
                for (HashMap.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    try {
                        json_obj.put(key,value);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                json_arr.put(json_obj);
            }
            return json_arr;
        }*/

        void testAddContact(){
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            int rawContactInsertIndex = ops.size();
            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat")
                    .withValue(RawContacts.ACCOUNT_NAME, "Account")
                    .withValue(Settings.UNGROUPED_VISIBLE, true)
                    .build());

            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, "Jyjy")
                    .build());
            try {
                getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }
            catch (Exception e){

            }
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... args) {
            JSONParser json = new JSONParser();

            params = new ArrayList<NameValuePair>();
            ArrayList<String> mContacts = new ArrayList<String>();
            //String temp ="{ \"contacts\": [{\"name\": \"saar\", \"phones\": [\"333666\",\"12345674\"]}]}";
            /*try {
                //String[] phones = new String[]{};
                ArrayList<String> phones = new ArrayList<String>();
                String MainNumber;
                Cursor peopleTemp;
                Cursor test = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                        null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
                while (test != null && test.moveToNext()) {
                    String id = test.getString(test.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = test.getString(test.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String[] projection = new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, };
                    peopleTemp = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, RawContacts.CONTACT_ID + "= ?",
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
                JSONArray res = json.getJSONArray("http://nodejs-whatnext.rhcloud.com/synccontacts", params);
                params.clear();
            }
            catch (Exception e){

            }*/
            //JSONObject mJson = new JSONObject();

            //params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            //JSONArray jAry = json.getJSONArray("http://nodejs-whatnext.rhcloud.com/getuser", params);
            //testAddContact();
            Cursor cur = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

            String id = null, name = null;
            HashMap<String, String> map;
            Cursor pCur = null, temp = null;
            String MainNumber="";//phonetype=""
            boolean hasWN,toAdd;
            boolean forDebug = true;
            while (cur.moveToNext()) {
                hasWN = false;
                toAdd = false;
                map = new HashMap<String, String>();
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                try {


                    //pCur = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                   //         null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                    //                + " = ?", new String[]{id}, null);

                    String[] projection    = new String[] {
                            RawContacts._ID,  ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER , RawContacts.ACCOUNT_TYPE};
                    Cursor people = getActivity().getContentResolver().query(  ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,   RawContacts.CONTACT_ID + "= ?",
                            new String[] { id }, null);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexAccountType = people.getColumnIndex(RawContacts.ACCOUNT_TYPE);
                    int counter= 0;
                    while (people.moveToNext()){
                        String accountType = people.getString(indexAccountType);
                        MainNumber = people.getString(indexNumber);
                        if(accountType != null && accountType.equals("learn2crack.chat.account")){
                            hasWN = true;

                            break;
                        }
                    }
                    if ((!name.equals("")) && (!MainNumber.equals(""))){
                        toAdd = true;
                        map.put("name", name);
                        map.put("mobno", MainNumber);
                        if(hasWN){
                            map.put("wn", "true");
                        }
                        else {
                            map.put("wn", "false");
                            if(forDebug) {
                                forDebug = false;
                                int backId = 0;
                                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
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
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MainNumber)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                        .build());
                                try{
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    final String msg ="name : " + name;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(),msg, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                }
                                catch (Exception e){
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    final String msg ="name : " + name;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(),msg, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    }

                    //boolean toAdd = false;
                    //while (pCur!= null && pCur.moveToNext()) {
                    //    String phonetype = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    //    MainNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                       // temp = getActivity().getContentResolver().query(RawContacts.CONTENT_URI,
                        //        null, RawContacts.ACCOUNT_TYPE +" = ?", new String[]{"learn2crack.chat.account"}, null);
                        //Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon()
                       //         .appendQueryParameter(RawContacts.ACCOUNT_NAME, "Account")
                        //        .appendQueryParameter(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account")
                        //        .build();
                        //temp = getActivity().getContentResolver().query(rawContactUri, null,null,null,null);
                       // temp = getActivity().getContentResolver().query(RawContacts.CONTENT_URI,new String[]{RawContacts.ACCOUNT_NAME, RawContacts.ACCOUNT_TYPE},
                        //        RawContacts.ACCOUNT_NAME + "=? and "+ RawContacts.ACCOUNT_TYPE + "=?",new String[]{"Account","learn2crack.chat.account"},null);
                       // while(temp.moveToNext()){
                       //     String tempName = temp.getString(temp.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY));
                       // }

                        //String accountType = temp.getString(cur.getColumnIndex(RawContacts.ACCOUNT_TYPE));

                        //if ((!name.equals("")) && (!MainNumber.equals(""))) {
                        //    map.put("mobno", MainNumber);
                            //final String msg ="name : " + name + " phone : " + MainNumber;
                           // map.put("mobno", MainNumber);
                            //toAdd = true;
                    /*
                         if(kk<1){
                        map.put("wn", "true");
                        kk++;
                        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                        int rawContactInsertIndex = ops.size();
                        int backId = 0;
                        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                                .withValue(RawContacts.ACCOUNT_NAME, "Account")
                                .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account")
                                .build());

                        ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                .withValueBackReference(Data.RAW_CONTACT_ID, backId)
                                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                .build());
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MainNumber)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        CommonDataKinds.Phone.TYPE_WORK_MOBILE)
                                .build());
                            /*if(kk<1){
                                map.put("wn", "true");
                                kk++;
                                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                                int rawContactInsertIndex = ops.size();
                                int backId = 0;
                                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                                        .withValue(RawContacts.ACCOUNT_NAME, "Account")
                                        .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat.account")
                                        .build());

                                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, backId)
                                        .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                        .build());
                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backId)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MainNumber)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                CommonDataKinds.Phone.TYPE_WORK_MOBILE)
                                        .build());

                                /*ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                                        .withValueBackReference(Data.RAW_CONTACT_ID, backId)
                                        .withValue(Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                        .withValue(Data.DATA1, 12345)
                                        .withValue(Data.DATA2, "data2")
                                        .withValue(Data.DATA3, "data3")
                                        .build());
                                Handler handler = new Handler(Looper.getMainLooper());
                                final String msg ="name : " + name;
                                 handler.post(new Runnable() {

                                   @Override
                                     public void run() {
                                         Toast.makeText(getActivity(),msg, Toast.LENGTH_LONG).show();
                                     }
                                 });

                                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                                        .withValue(RawContacts.ACCOUNT_TYPE, "learn2crack.chat")
                                        .withValue(RawContacts.ACCOUNT_NAME, "Account")
                                                //.withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                                //.withValue(Settings.UNGROUPED_VISIBLE, true)
                                        .build());*/
                                /*ops.add(ContentProviderOperation.newInsert(
                                        ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(
                                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                                name).build());

                                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI).withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                        .withValue(Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                        //.withValue(ContactsContract.Data.DATA1, "")//TODO:fill with gcd num
                                        .build());
                                try {
                                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                }
                                catch (Exception e){
                                    kk--;

                                }
                            } else{
                                map.put("wn", "false");
                            }*/

                       // }
                       // break;
                    //}
                    if(toAdd) {
                        users.add(map);
                    }
                }
                catch (Exception e){

                }

                if(pCur == null){

                }
            }
            if(pCur != null) {
                pCur.close();
            }
            if(cur != null){
                cur.close();
            }
            //phones.close();
            return users;
            //JSONObject obj=new arrayLToJSON(users);
            //return  arrayLToJSON(users);
        }
        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> res) {
            /*for(int i = 0; i < json.length(); i++){
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    String name = c.getString("name");
                    String mobno = c.getString("mobno");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("mobno", mobno);
                    users.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }*/
            ListAdapter adapter = new UserAdapter(getActivity(), users,
                    R.layout.user_list_single,
                    new String[] { "name","mobno" , "wn" }, new int[] {
                    R.id.name, R.id.mobno , R.id.micon});
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if(users.get(position).get("wn").equals("true")) {
                        Bundle args = new Bundle();
                        args.putString("mobno", users.get(position).get("mobno"));
                        args.putString("name", users.get(position).get("name"));
                        Intent chat = new Intent(getActivity(), MessageActivity.class);
                        chat.putExtra("INFO", args);
                        startActivity(chat);
                    }
                }
            });
        }
    }
    private class Logout extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/logout",params);

            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String res = null;
            try {
                res = json.getString("response");
                Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();
                if(res.equals("Removed Sucessfully")) {
                    Fragment reg = new LoginFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", "");
                    edit.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

}