package learn2crack.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    public ViewPager viewPager;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "681641134962";
    Fragment reg = null;

    static final String TAG = "WN";
    //new matrial design
    //private Toolbar mToolbar;
    //private FragmentDrawer drawerFragment;

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid;
    UserFragment userFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            userFragment = new UserFragment();
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

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

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

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());


        context = getApplicationContext();

        //adapter.addFragment(new LoginFragment(), "Login Fragment");

        String reg_from  = prefs.getString("REG_FROM", "");
        String reg_name  = prefs.getString("FROM_NAME", "");
        String reg_uuid  = prefs.getString("UUID", "");
        String reg_phone = prefs.getString("PHONE_NUMBER", "");
        String reg_id    = prefs.getString("REG_ID", "");


        Toast.makeText(getApplicationContext(), "reg_from = " + reg_from, Toast.LENGTH_LONG).show();

        Log.i(TAG, "reg_from " + reg_from);
        Log.i(TAG,"reg_name " + reg_name);
        Log.i(TAG,"reg_uuid " + reg_uuid);
        Log.i(TAG,"reg_phone " + reg_phone);
        Log.i(TAG,"reg_id " + reg_id);


        if (!reg_from.isEmpty()) {
            Toast.makeText(getApplicationContext(), "reg_from is no empty", Toast.LENGTH_LONG).show();
            fragmentAdapter.addFragment(userFragment, "User Fragment");
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
                                fragmentAdapter.addFragment(userFragment, "User");
                                break;
                            }
                            case R.id.nav_friends:
                            {
                                fragmentAdapter.addFragment(new EmptyFragment(), "Friends");
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
    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        if(id == R.id.action_search){
//            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);


        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }




//    @Override
//    public void onDrawerItemSelected(View view, int position) {
//        displayView(position);
//    }

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
        fragmentAdapter.addFragment(userFragment, "User");
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
            //Toast.makeText(getApplicationContext(), "Logout is clicked!", Toast.LENGTH_SHORT).show();

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

            String res = null;
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
