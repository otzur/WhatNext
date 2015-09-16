package learn2crack.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import learn2crack.adapters.FragmentAdapter;
import learn2crack.chat.R;
import learn2crack.cheese.CheeseListFragment;
import learn2crack.utilities.JSONParser;


public class MainActivity extends AppCompatActivity implements
        ContactsListFragment.OnContactsInteractionListener{

    private DrawerLayout mDrawerLayout;
    public ViewPager viewPager;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "681641134962";

    private static Context context;

    Fragment reg = null;

    static final String TAG = "WN";
    //new matrial design
    //private Toolbar mToolbar;
    //private FragmentDrawer drawerFragment;

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    //private static Context context;
    String regid;
    //UserFragment contactListFragment ;
    ContactsListFragment contactListFragment;

    // True if this activity instance is a search result view (used on pre-HC devices that load
    // search results in a separate instance of the activity rather than loading results in-line
    // as the query is typed.
    private boolean isSearchResultView = false;
    private Uri mContactUri=null;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        try {
            setContentView(R.layout.activity_main);

            //contactListFragment = new UserFragment();
            contactListFragment = new ContactsListFragment();
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final ActionBar ab = getSupportActionBar();
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView != null) {
                setupDrawerContent(navigationView);
            }

            prefs = getSharedPreferences("Chat", 0);
            String userName = prefs.getString("FROM_NAME", "");
            TextView userTextView  = (TextView)findViewById(R.id.userName);


            if (!userName.isEmpty()) {
                userTextView.setText(userName);
            }

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            if (viewPager != null) {
                setupViewPager(viewPager);
            }

            /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });*/

//            drawerFragment = (FragmentDrawer)
//                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
//            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
//            drawerFragment.setDrawerListener(this);

            // display the first navigation drawer view on app launch
//            displayView(0);

//            prefs = getSharedPreferences("Chat", 0);
//            context = getApplicationContext();
//            if (!prefs.getString("REG_FROM", "").isEmpty()) {
//
//                Fragment user = new UserFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.container_body, user);
//                fragmentTransaction.commit();
//
//            } else {
//                if (validateRegistration()) {
//
//                    reg = new LoginFragment();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.container_body, reg);
//                    fragmentTransaction.commit();
//                }
//            }
        }
        catch (Exception e){
            int num = 5;
        }
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact has been selected.
     *
     * @param contactUri The contact Uri to the selected contact.
     */
    @Override
    public void onContactSelected(Uri contactUri) {
        mContactUri = contactUri;
        /*if (isTwoPaneLayout && mContactDetailFragment != null) {
            // If two pane layout then update the detail fragment to show the selected contact
            mContactDetailFragment.setContact(contactUri);
        } else {
            // Otherwise single pane layout, start a new ContactDetailActivity with
            // the contact Uri
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.setData(contactUri);
            startActivity(intent);
        }*/
        //TODO: let the user pick the number
        /*
        String[] whereArgs = new String[] { String.valueOf(contactUri) };
        Log.d(TAG, String.valueOf(contactUri));
        Cursor cursor2=null;
        String id="";
        int idx;
        try {
            cursor2 = getContentResolver().query(contactUri, null, null, null, null);
            if (cursor2.moveToFirst()) {
                idx = cursor2.getColumnIndex(ContactsContract.Contacts._ID);
                id = cursor2.getString(idx);
            }
                cursor2 = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+id,null, null);
    }
        catch (Exception ex){
            Log.e("wn",ex.getLocalizedMessage());
        }

        int phoneNumberIndex = cursor2
                .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

        Log.d(TAG, String.valueOf(cursor2.getCount()));
        String phoneNumber="";
        if (cursor2.moveToFirst()) {
            phoneNumberIndex = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNumber = cursor2.getString(phoneNumberIndex);
        }
        if (cursor2 != null) {
            Log.v(TAG, "Cursor Not null");
            try {
                if (cursor2.moveToNext()) {
                    Log.v(TAG, "Moved to first");
                    Log.v(TAG, "Cursor Moved to first and checking");
                    phoneNumber = cursor2.getString(phoneNumberIndex);
                }
            } finally {
                Log.v(TAG, "In finally");
                cursor2.close();
            }


            Bundle args = new Bundle();
            args.putString("mobno", phoneNumber);
            //args.putString("name", (String) text1.getText());
            args.putString("type", "HimAndHer");
            args.putString("status", "new");
            Intent chat = new Intent(this , WnMessageNewActivity.class);
            chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            chat.putExtra("INFO", args);
            context.startActivity(chat);
        }*/
    }

    /**
     * This interface callback lets the main contacts list fragment notify
     * this activity that a contact is no longer selected.
     */
    @Override
    public void onSelectionCleared() {
        /*if (isTwoPaneLayout && mContactDetailFragment != null) {
            mContactDetailFragment.setContact(null);
        }*/
    }

    @Override
    public boolean onSearchRequested() {
        // Don't allow another search if this activity instance is already showing
        // search results. Only used pre-HC.
        return !isSearchResultView && super.onSearchRequested();
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        context = getApplicationContext();
        String reg_from  = prefs.getString("REG_FROM", "");
        String reg_name  = prefs.getString("FROM_NAME", "");
        String reg_uuid  = prefs.getString("UUID", "");
        String reg_phone = prefs.getString("PHONE_NUMBER", "");
        String reg_id    = prefs.getString("REG_ID", "");

        if (!reg_from.isEmpty()) {
            fragmentAdapter.addFragment(contactListFragment, "User Fragment");
        }
        else
        {
            fragmentAdapter.addFragment(new LoginFragment(), "Login Fragment");
        }
        viewPager.setAdapter(fragmentAdapter);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());

                        switch (menuItem.getItemId())
                        {
                            case R.id.nav_home:
                            {
                                fragmentAdapter.addFragment(contactListFragment, "User");
                                break;
                            }
                            case R.id.nav_friends:
                            {
                                fragmentAdapter.addFragment(contactListFragment, "User");
                                break;
                            }

                            case R.id.nav_history:
                            {
                                fragmentAdapter.addFragment(new HistoryFragment(), "Messages");
                                break;
                            }

                            case R.id.nav_discussion:
                            {
                                fragmentAdapter.addFragment(new CheeseListFragment(), "Cheese List");
                                break;
                            }

                            case R.id.nav_history2:
                            {
                                fragmentAdapter.addFragment(new HistoryFragment2(), "History List");
                                break;
                            }

                            case R.id.nav_conversation:
                            {
                                fragmentAdapter.addFragment(new ConversationFragment(), "Conversation List");
                                break;
                            }

                            case R.id.nav_cardview_example:
                            {
                                fragmentAdapter.addFragment(new RecyclerViewActivity(), "Card View Example");
                                break;
                            }

                            case R.id.nav_empty_example:
                            {
                                fragmentAdapter.addFragment(new EmptyFragment(), "Empty Example");
                                break;
                            }
                            case R.id.nav_logout:
                            {
                                new  Logout().execute();
                                break;
                            }
                        }


                       //adapter.addFragment(new MessagesFragment(), "MessagesFragment 1");
                        viewPager.setAdapter(fragmentAdapter);

                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getTag(R.id.TAG_CONTACT_ID) == null) {
            // context menu logic
            return;
        }
        Cursor cursor2 = null;
        String contactID = (String)v.getTag(R.id.TAG_CONTACT_ID);
       boolean hasWn = (Boolean)(v.getTag(R.id.TAG_HAS_WN));
        try {
            if (hasWn) {
                cursor2 = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.Data.RAW_CONTACT_ID + "=?",
                        new String[]{contactID}, null);
                SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("CONTACT_HAS_WN", "1");
                edit.commit();
            } else {
                cursor2 = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.Data.CONTACT_ID + "=?",
                        new String[]{contactID}, null);
                SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("CONTACT_HAS_WN", "0");
                edit.commit();
            }


            int numOfPhones = cursor2.getCount();
            String phoneNumber = "";
            if(numOfPhones>1) {
                while (numOfPhones > 0) {
                    cursor2.moveToFirst();
                    phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    menu.add(phoneNumber);
                    numOfPhones--;
                }
                return;
            }
            cursor2.moveToFirst();
            if(numOfPhones > 0) {
                phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            if(hasWn){
                Bundle args = new Bundle();
                args.putString("mobno", phoneNumber);
                args.putString("type", "HimAndHer");
                args.putString("status", "New");
                Intent chat = new Intent(context, WnMessageNewActivity.class);
                chat.putExtra("INFO", args);
                chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(chat);
            }
            else{
                Intent intentt = new Intent(Intent.ACTION_VIEW);
                intentt.setData(Uri.parse("sms:"));
                intentt.setType("vnd.android-dir/mms-sms");
                //intentt.putExtra(Intent.EXTRA_TEXT, "invitation to use wn");
                intentt.putExtra("address",  phoneNumber);
                intentt.putExtra("sms_body", "invitation to use wn");
                intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentt);
            }
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        finally {
            cursor2.close();
        }

        /*String[] whereArgs = new String[]{String.valueOf(mContactUri)};
        Log.d(TAG, String.valueOf(mContactUri));
        Cursor cursor2 = null;
        String id = "";
        int idx;
        try {
            cursor2 = getContentResolver().query(mContactUri, null, null, null, null);
            if (cursor2.moveToFirst()) {
                idx = cursor2.getColumnIndex(ContactsContract.Contacts._ID);
                id = cursor2.getString(idx);
            }
            cursor2 = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
        } catch (Exception ex) {
            Log.e("wn", ex.getLocalizedMessage());
        }

        int phoneNumberIndex = cursor2
                .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

        Log.d(TAG, String.valueOf(cursor2.getCount()));
        String phoneNumber = "";
        if (cursor2.moveToFirst()) {
            phoneNumberIndex = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNumber = cursor2.getString(phoneNumberIndex);
        }
        if (cursor2 != null) {
            Log.v(TAG, "Cursor Not null");
            try {
                if (cursor2.moveToNext()) {
                    Log.v(TAG, "Moved to first");
                    Log.v(TAG, "Cursor Moved to first and checking");
                    phoneNumber = cursor2.getString(phoneNumberIndex);
                }
                menu.add(phoneNumber);
            } finally {
                Log.v(TAG, "In finally");
                cursor2.close();
            }

        }*/
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
        //String hasWN = prefs.getString("CONTACT_HAS_WN", "0");
        String phone = item.getTitle().toString();
        //if(hasWN.equals("1")) {

            Bundle args = new Bundle();
            args.putString("mobno", phone);
            args.putString("type", "HimAndHer");
            args.putString("status", "New");
            Intent chat = new Intent(context, WnMessageNewActivity.class);
            chat.putExtra("INFO", args);
            chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(chat);
        /*}
        else{
            Intent intentt = new Intent(Intent.ACTION_VIEW);
            intentt.setData(Uri.parse("sms:"));
            intentt.setType("vnd.android-dir/mms-sms");
            intentt.putExtra(Intent.EXTRA_TEXT, "invitation to use wn");
            intentt.putExtra("address",  phone);
            intentt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentt);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean validateRegistration(){
        if(prefs.getString("REG_ID", "").equals("")){
            if(checkPlayServices()){
                new Register().execute();
                return true;
            }else{
                Toast.makeText(getApplicationContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
                return  false;
            }
        }
        return true;
    }

    public void changeToUserScreen()
    {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        //fragmentAdapter.addFragment(contactListFragment, "User");
        fragmentAdapter.addFragment(contactListFragment, "User");
        viewPager.setAdapter(fragmentAdapter);

    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
            Bundle extras = intent.getExtras();
            if (extras == null)
                return;

            String phoneNumber = prefs.getString("REG_FROM", "");
            if(phoneNumber.equals(""))
                return;

            String uniqueKey = prefs.getString("UUID", "");
            if(phoneNumber.isEmpty())
                return;

            Object[] pdus = (Object[]) extras.get("pdus");
            SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
            String origNumber = msg.getOriginatingAddress();
            String msgBody = msg.getMessageBody();
            if(!msgBody.equals("Activation:"+uniqueKey))
                return;
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("MOB_NUM_ACTIVE", "true");
            edit.commit();
        }
    };

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private class Register extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regid = gcm.register(SENDER_ID);
                    Log.e("RegId",regid);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_ID", regid);
                    edit.commit();

                }

                return  regid;

            } catch (IOException ex) {
                Log.e("Error", ex.getMessage());
                return "Fails";

            }
        }
        @Override
        protected void onPostExecute(String json) {

            Fragment reg = new LoginFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.viewpager, reg);
            fragmentTransaction.commit();
            /////////////////////
//            Fragment reg = new LoginFragment();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.replace(R.id.container_body, reg);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            ft.addToBackStack(null);
//            ft.commit();
        }
    }


    private class Logout extends AsyncTask<String, String, JSONObject> {

        private List<NameValuePair> params;

        @Override
        protected JSONObject doInBackground(String... args) {
            params = new ArrayList<NameValuePair>();
            Log.i("WN", "Logout clicked");
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM", "")));
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/logout",params);

            Log.i("WN", "Logout sent");

            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("REG_FROM", "");
            edit.putString("REG_ID", "");
            edit.commit();


            //JSONObject jObj;
            jObj = new JSONObject();
            try {
                jObj.put("response", "Removed Sucessfully");
               // return jObj;
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String res;
            try {
                res = json.getString("response");
                if(res.equals("Removed Sucessfully")) {
//                    Fragment reg = new LoginFragment();
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.viewpager, reg);
//                    ft.setTransition(FragmentTransaction.TRANSIT_NONE);
//                    ft.addToBackStack(null);
//                    ft.commit();

//                    ////////////////
//                    Fragment login = new LoginFragment();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.viewpager, login);
//                    fragmentTransaction.commit();

                    FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
                    fragmentAdapter.addFragment(new LoginFragment(), "Login Fragment");
                    viewPager.setAdapter(fragmentAdapter);

//                    SharedPreferences.Editor edit = prefs.edit();
//                    edit.putString("REG_FROM", "");
//                    edit.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
