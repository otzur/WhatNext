package learn2crack.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import learn2crack.bl.ObjectManager;
import learn2crack.bl.OptionSelectorManager;
import learn2crack.chat.R;
import learn2crack.cheese.Cheeses;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageStatus;
import learn2crack.utilities.Contacts;
import learn2crack.utilities.JSONParser;

/**
 * Created by otzur on 6/18/2015.
 */

public class WnMessageReceiveActivity extends AppCompatActivity {


    Bundle bundle;
    //ImageButton btnSend;
    SharedPreferences prefs;
    List<NameValuePair> params;
    static final String TAG = "WN";

    //private String selectedTab;
    //private String c_id;
    //private String conversation_guid;
    private  WnConversation wnConversation;
    private String msg_id="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_receive_layout);
        Intent intent = getIntent();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");


//        if (bundle.getString("tab") != null) {
//
//            selectedTab = bundle.getString("tab");
//        }

//        if (bundle.getString("conversation_guid") != null) {
//            conversation_guid =bundle.getString("conversation_guid");
//        }

//        if (bundle.getString("c_id") != null) {
//            c_id =bundle.getString("c_id");
//        }

        if(bundle.getSerializable("conversation") != null){

            wnConversation = (WnConversation) bundle.getSerializable("conversation");
            if(wnConversation.getMessages().size() > 0)
                msg_id = wnConversation.getMessages().get(0).getMessage_guid();
            //tvUserName.setText(wnConversation.getContacts().get(0).name);
            Log.i(TAG,"Inside Recv Activity" );
            Log.i(TAG,"got conversation Serializable" );
            Log.i(TAG, "status = " + wnConversation.getStatus());
            Log.i(TAG, "Conversation_guid = " + wnConversation.getConversation_guid());
            Log.i(TAG, "Contact phone = " + wnConversation.getContacts().get(0).getPhoneNumber());
        }


        //TODO: check- if response we want only 1 option according to first message
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        //tabLayout.setupWithViewPager(viewPager);

        loadBackdrop();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new Send(viewPager.getCurrentItem()).execute();
                WnMessageRowOptionFragment of = getVisibleFragment();
                String selected_options = of.getSelectedOptions();
                new Send(selected_options).execute();
                Snackbar.make(view, selected_options, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //finish();
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

    private WnMessageRowOptionFragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // OptionFragment fr = (OptionFragment)getFragmentManager().findFragmentById(R.id.main_content);
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments.isEmpty())
            return null;
        else {
            return (WnMessageRowOptionFragment) fragments.get(1);
        }

        //return fr;
    }


    private class Send extends AsyncTask<String, String, JSONObject> {


        private String selected;
        private String from;
        //private String from_name;
        private String user_phone;
        private String user_name;
        private String type;
        private String status;
        private String selected_options;

        public Send(String selected_options) {
            Log.i(TAG, "selected_options  = " + selected_options);
            selected = selected_options;

            from =  prefs.getString("REG_FROM","");
            Log.i(TAG, "from user_phone   = " + from);
            //from_name = "Me";
            //Log.i(TAG, "from name  = " + from_name);

            user_phone = wnConversation.getContacts().get(0).getPhoneNumber();
            //user_phone = bundle.getString("mobno");
            Log.i(TAG,"user_phone = " + user_phone);
            user_name =  Contacts.getContactName(getApplicationContext(), user_phone);
            Log.i(TAG,"user_phone name = " + user_name);

            //type = bundle.getString("type");
            type = wnConversation.getType();
            Log.i(TAG, "type = " + type);

            //status = "Response";
            wnConversation.setStatus(WnMessageStatus.RESPONSE);
            Log.i(TAG, "status = " + status);

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            //UUID uuid = UUID.randomUUID();

            params = new ArrayList<>();
            params.add((new BasicNameValuePair("msg_id", msg_id)));
            params.add(new BasicNameValuePair("fromu", from));
            params.add(new BasicNameValuePair("to", user_phone.replaceAll("[^0-9]", "")));

            params.add(new BasicNameValuePair("user_name", user_name));


            params.add(new BasicNameValuePair("tab", "" + wnConversation.getTab()));
            params.add(new BasicNameValuePair("type", "" + wnConversation.getType()));
            params.add(new BasicNameValuePair("status", "" + wnConversation.getStatus()));
            params.add(new BasicNameValuePair("c_id", ""+ wnConversation.getConversation_guid()));
            WnMessageRowOptionFragment of = getVisibleFragment(0);
            selected_options = of.getSelectedOptions();
            params.add((new BasicNameValuePair("selected_options", selected_options)));
            Log.i(TAG, "selected_options = " + selected_options);

            Log.i(TAG + "RecvActivity", "conversation_guid = " + wnConversation.getConversation_guid());
            Log.i(TAG + "RecvActivity", "c_id = " + wnConversation.getRowId());

            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/send", params);

            //Log.i(TAG, "from_name = " + from_name);

//            WnConversation wnConversation = null;
            wnConversation.setStatus(WnMessageStatus.RESULTS);
            WnMessage wnMessage  =  ObjectManager.createNewMessage(msg_id,user_phone, selected_options, WnMessageStatus.RESULTS, 1);
            wnConversation.addMessage(wnMessage);
            ObjectManager.updateConversation(MainActivity.getAppContext(), wnConversation);
//            dbConversations.open();
//            dbConversations.update(conversation_guid, 0, type, selectedTab, user_phone, user_name, "RESULTS");
//            dbConversations.close();
//
//            dba.open();
//            dba.insert(uuid.toString(), "message", user_phone, selected_options ,"RESULTS", 1, c_id);// Insert record in your DB
//            dba.close();
            return jObj;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            String res;
            try {
                res = json.getString("response");
                if (res.equals("Failure")) {
                    Toast.makeText(getApplicationContext(), "The user_phone has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle args = new Bundle();
                    args.putString("c_id", Long.valueOf(wnConversation.getRowId()).toString());
                    args.putInt("numberOfOptions", wnConversation.getTab());
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

        private WnMessageRowOptionFragment getVisibleFragment(int index) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments.isEmpty())
                return null;
            else {
                return (WnMessageRowOptionFragment) fragments.get(index + 1);
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
            bundl.putInt("numberOfOptions", OptionSelectorManager.getNumberOfOptionsByTab(wnConversation.getTab()));
//            switch (wnConversation.getTab()) {
//                case 0: {
//                    bundl.putInt("numberOfOptions", 2);
//                    //return new HomeFragment();
//                }
//                break;
//                case 1: {
//                    bundl.putInt("numberOfOptions", 5);
//                    //return new MessagesFragment();
//                }
//                ;
//                break;
//                case 2:
//                default: {
//                    bundl.putInt("numberOfOptions", 8);
//                    //return new FriendsFragment();
//                }
//                ;
//                break;
//            }
            wnMessageRowOptionFragment.setArguments(bundl);
            return wnMessageRowOptionFragment;
        }

        @Override
        public int getCount() {
            return 1;
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
