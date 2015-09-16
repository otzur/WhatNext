package learn2crack.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import learn2crack.chat.R;
import learn2crack.cheese.Cheeses;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnConversation;
import learn2crack.utilities.Contacts;
import learn2crack.utilities.JSONParser;

/**
 * Created by otzur on 6/18/2015.
 */

public class WnMessageNewActivity extends AppCompatActivity {


    Bundle bundle;
    SharedPreferences prefs;
    WnConversation wnConversation;
    List<NameValuePair> params;
    static final String TAG = "WN";
    public enum wn_message_status {
        new_message,
        new_received,
        new_response
    }

    private String selectedTab ;
    private String from;
    private String to;
    private String type;
    private String status;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_new_layout);


        Intent intent = getIntent();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
       // TextView tvUserName = (TextView)findViewById(R.id.userName);
        //btnSend = (ImageButton) findViewById(R.id.btnSend);

        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");

        if(bundle.getString("mobno") != null)
        {
            Log.i(TAG,"mobno = " + (bundle.getString("mobno")));
        }
        if(bundle.getString("name") != null){

            //tvUserName.setText(bundle.getString("name"));
            collapsingToolbar.setTitle(bundle.getString("name"));
        }

        if(bundle.getString("tab") != null && bundle.getString("selected_options") != null){

            selectedTab = bundle.getString("tab");
        }

        if(bundle.getString("type") != null){

            type = bundle.getString("type");
        }

        if(bundle.getString("status") != null){

            status = bundle.getString("status");
        }


        //TODO: check- if response we want only 1 option according user first message
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        loadBackdrop();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Send(viewPager.getCurrentItem()).execute();

                //Snackbar.make(view, "WN Message Sent", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //finish();
            }
        });

        Log.i(TAG, "status = " + status);

        /*switch (status){

            case "new":
                break;

            case "received":
            {

                Log.i(TAG, "selectedTab = " + selectedTab);
                viewPager.setCurrentItem(Integer.parseInt(selectedTab));
                tabLayout.setVisibility(View.INVISIBLE);
                break;
            }
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items user the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

        private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    private class Send extends AsyncTask<String, String, JSONObject> {

        MessageDataSource dba=new MessageDataSource(getApplicationContext());//Create this object in onCreate() method
        ConversationDataSource dbConversations = new ConversationDataSource(getApplicationContext());

        private int selectedTab;
        private String from;
        private String from_name;
        private String user;
        private String user_name;
        private String type;
        private String status;
        private String selected_options;


        public Send(int currentItem) {
            Log.i(TAG,"Tab selected  = " + currentItem);
            selectedTab = currentItem;
            from =  prefs.getString("REG_FROM","");
            from_name = "Me";
            Log.i(TAG,"from user   = " + from);

            user = bundle.getString("mobno");
            Log.i(TAG,"user user = " + user);
            user_name =  Contacts.getContactName(getApplicationContext(), user);
            Log.i(TAG,"user name = " + user_name);

            type  = bundle.getString("type");
            Log.i(TAG,"type = " + type);
            status  = bundle.getString("status");
            Log.i(TAG,"status = " + status);

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            UUID uuid = UUID.randomUUID();
            UUID c_u_id = UUID.randomUUID();
            dbConversations.open();
            wnConversation= dbConversations.insert(c_u_id.toString(),2, selectedTab + 1, type, Integer.toString(selectedTab), user, user_name);
            dbConversations.close();
            params = new ArrayList<>();
            params.add((new BasicNameValuePair("msg_id",uuid.toString())));
            params.add(new BasicNameValuePair("fromu",from));
            params.add(new BasicNameValuePair("from_name",from_name));
            params.add(new BasicNameValuePair("to", user));
            params.add(new BasicNameValuePair("user_name", user_name));
            params.add(new BasicNameValuePair("tab", ""+ selectedTab));
            params.add(new BasicNameValuePair("type", "" + type));
            params.add(new BasicNameValuePair("status", "" + status));
            params.add(new BasicNameValuePair("c_id", c_u_id.toString()));
            WnMessageRowOptionFragment of = getVisibleFragment(selectedTab);
            selected_options = of.getSelectedOptions();
            params.add((new BasicNameValuePair("selected_options", selected_options)));
            Log.i(TAG, "selected_options = " + of.getSelectedOptions());
            Log.i(TAG, "type ==== " +type);
            //}


            //MESSAGE SENDING
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/send", params);

            dba.open();
            dba.insert(uuid.toString(), "message", user, user_name, selected_options ,type, "Sent", 1, wnConversation.getId() );// Insert record in your DB
            dba.close();

            Log.i(TAG, "saved in databased");

            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            //chat_msg.setText("");

            String res;
            try {


                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(), "The user has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bundle args = new Bundle();
                    args.putString("c_id", Long.toString(wnConversation.getId()));
                    Intent chat = new Intent(getApplicationContext(), ResultActivity.class);
                    chat.putExtra("INFO", args);
                    chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    getApplicationContext().startActivity(chat);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private WnMessageRowOptionFragment getVisibleFragment(int index){
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if(fragments.isEmpty())
                return null;
            else
            {
                return (WnMessageRowOptionFragment)fragments.get(index + 1);
            }
        }
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            WnMessageRowOptionFragment wnMessageRowOptionFragment = new WnMessageRowOptionFragment();
            Bundle bundl = new Bundle();
            switch (position) {
                case 0: {
                    bundl.putInt("numberOfOptions", 2);
                    //return new HomeFragment();
                }break;
                case 1: {
                    bundl.putInt("numberOfOptions", 5);
                    //return new MessagesFragment();
                };break;
                case 2:
                default:
                {
                    bundl.putInt("numberOfOptions", 8);
                    //return new FriendsFragment();
                };break;
            }
            wnMessageRowOptionFragment.setArguments(bundl);
            return wnMessageRowOptionFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Coin";
                case 1:
                    return "Roulette";
                case 2:
                default:
                    return "Table";
            }
        }
    }
}
