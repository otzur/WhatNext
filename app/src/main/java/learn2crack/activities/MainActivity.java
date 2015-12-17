package learn2crack.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.util.LongSparseArray;
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
import learn2crack.bl.ObjectManager;
import learn2crack.chat.R;
import learn2crack.cheese.CheeseListFragment;
import learn2crack.cotacts.Contact;
import learn2crack.db.DatabaseHelper;
import learn2crack.models.WnConversation;
import learn2crack.utilities.JSONParser;


public class MainActivity extends AppCompatActivity implements
        WnContactsListFragment.OnContactsInteractionListener{

    private DrawerLayout mDrawerLayout;
    public ViewPager viewPager;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "681641134962";

    private static Context context ;

    Fragment reg = null;

    final String TAG = "WN ";// + getLocalClassName();
    //new matrial design
    //private Toolbar mToolbar;
    //private FragmentDrawer drawerFragment;

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    //private static Context context;
    String regid;
    //UserFragment contactListFragment ;
    WnContactsListFragment contactListFragment;

    // True if this activity instance is a search result view (used on pre-HC devices that load
    // search results in a separate instance of the activity rather than loading results in-line
    // as the query is typed.
    private boolean isSearchResultView = false;
    private Uri mContactUri=null;
    private SQLiteDatabase db;
    private DatabaseHelper DBHelper;

    public static Context getAppContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        try {
            setContentView(R.layout.activity_main);

            //contactListFragment = new UserFragment();
            contactListFragment = new WnContactsListFragment();
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

                        switch (menuItem.getItemId()) {
                           /* case R.id.nav_home: {
                                //fragmentAdapter.addFragment(contactListFragment, "User");
                                //break;
                                fragmentAdapter.addFragment(new ConversationFragment(), "Conversation List");
                                break;
                            }*/
                            case R.id.nav_friends: {
                                fragmentAdapter.addFragment(contactListFragment, "User");
                                break;
                            }

                            /*case R.id.nav_history: {
                                fragmentAdapter.addFragment(new HistoryFragment(), "Messages");
                                break;
                            }

                            case R.id.nav_discussion: {
                                fragmentAdapter.addFragment(new CheeseListFragment(), "Cheese List");
                                break;
                            }

                            case R.id.nav_history2: {
                                fragmentAdapter.addFragment(new HistoryFragment2(), "History List");
                                break;
                            }
                            */
                            case R.id.nav_conversation:
                            {
                                fragmentAdapter.addFragment(new ConversationFragment(), "Conversation List");
                                break;
                            }

                            /*case R.id.nav_cardview_example:
                            {
                                fragmentAdapter.addFragment(new RecyclerViewActivity(), "Card View Example");
                                break;
                            }

                            case R.id.nav_empty_example:
                            {
                                fragmentAdapter.addFragment(new EmptyFragment(), "Empty Example");
                                break;
                            }*/
                            case R.id.nav_logout: {
                                new Logout().execute();
                                break;
                            }
                            case R.id.nav_db:{

                                DBHelper = DatabaseHelper.getInstance(getAppContext());
                                db = DBHelper.getWritableDatabase();
                                DBHelper.onUpgrade(db, 0, 1);


                                Snackbar.make(navigationView, "Database deleted", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

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
        //phoneNumber="";
        //alreadyShowedNumbers = false;
        return true;
    }

    boolean alreadyShowedNumbers = false;
    String phoneNumber = ""; // for context menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getTag(R.id.TAG_CONTACT_ID) == null) {
            // context menu logic
            return;
        }
        Contact contact = (Contact)v.getTag(R.id.TAG_CONTACT);
        if (contact == null) {
            // context menu logic
            return;
        }
        try {
            if(!alreadyShowedNumbers) {
                SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("CONTACT_HAS_WN", "1");
                edit.commit();
                int numOfPhones = contact.getPhonesSize();//cursor2.getCount();
                if (numOfPhones > 1) {
                    LongSparseArray<String> phones = contact.getPhones();
                    int phonesSize = phones.size();
                    for (int i = 0; i < phonesSize; i++) {
                        menu.add(phones.valueAt(i));
                    }
                    phoneNumber = contact.getPhones().valueAt(0);
                } else {
                    if (numOfPhones >= 1) {
                        phoneNumber = contact.getPhones().valueAt(0);
                    }
                    menu.add("Him and Her");
                    menu.add("Custom");
                    alreadyShowedNumbers = true;
                }
            } else {
                menu.add("Him and Her");
                menu.add("Custom");
            }

            /*
            String type = "HimAndHer";
            Log.i(TAG, "Start new activity");
            WnConversation wnConversation =  ObjectManager.createNewConversation(MainActivity.getAppContext(), phoneNumber, type);
            Bundle args = new Bundle();
            args.putSerializable("conversation", wnConversation);
            Intent chat = new Intent(context, WnMessageNewActivity.class);
            chat.putExtra("INFO", args);
            chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(chat);*/
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //SharedPreferences prefs = context.getSharedPreferences("Chat", 0);
        String type="Custom";
        Class targerClass = null;
        if(phoneNumber.equals("")) {
            phoneNumber = item.getTitle().toString();
            View temp = item.getActionView();
            registerForContextMenu(temp);
            //temp.setTag(R.id.TAG_CONTACT_ID, v.getTag(R.id.TAG_CONTACT_ID));
            //temp.setTag(R.id.TAG_CONTACT, v.getTag(R.id.TAG_CONTACT));
            temp.showContextMenu();
            unregisterForContextMenu(temp);
            return true;
            //String phone = item.getTitle().toString();
            //String mobno = phone;
        } else {
            switch (item.getTitle().toString()){
                case "Him and Her":
                    type = "HimAndHer";
                    targerClass = WnMessageNewActivity.class;
                    break;
                case "Custom":
                default:
                    type = "Custom";
                    targerClass = WnMessageCustomActivity.class;
                    break;
            }
        }
        //String type = "HimAndHer";
        WnConversation wnConversation =  ObjectManager.createNewConversation( MainActivity.getAppContext(), phoneNumber, type);
        Bundle args = new Bundle();
        args.putSerializable("conversation", wnConversation);


        Intent chat = new Intent(context, targerClass);
        chat.putExtra("INFO", args);
        chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alreadyShowedNumbers = false;
        phoneNumber = "";
        context.startActivity(chat);
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

            Log.i("WN", "Logout SENT");

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
