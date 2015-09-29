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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import learn2crack.bl.ObjectManager;
import learn2crack.chat.R;
import learn2crack.cheese.Cheeses;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageStatus;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_new_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TextView tvUserName = (TextView)findViewById(R.id.userName);
        //btnSend = (ImageButton) findViewById(R.id.btnSend);

        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");

//        if(bundle.getString("mobno") != null)
//        {
//            Log.i(TAG,"mobno = " + (bundle.getString("mobno")));
//        }
//        if(bundle.getString("name") != null){
//
//            tvUserName.setText(bundle.getString("name"));
//            collapsingToolbar.setTitle(bundle.getString("name"));
//        }
//
//        if(bundle.getString("tab") != null && bundle.getString("selected_options") != null){
//
//            selectedTab = bundle.getString("tab");
//        }
//
//        if(bundle.getString("type") != null){
//
//            type = bundle.getString("type");
//        }
//
//        if(bundle.getString("status") != null){
//
//            status = bundle.getString("status");
//        }

        if(bundle.getSerializable("conversation") != null){

            wnConversation = (WnConversation) bundle.getSerializable("conversation");
            //tvUserName.setText(wnConversation.getContacts().get(0).name);
            Log.i(TAG, "Inside new Activity");
            Log.i(TAG,"got conversation Serializable" );
            Log.i(TAG, "status = " + wnConversation.getStatus());
            Log.i(TAG, "Conversation_guid = " + wnConversation.getConversation_guid());
            Log.i(TAG, "User name= " + wnConversation.getContacts().get(0).getName());
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
            }
        });
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

        private String from;
        private String selected_options;


        public Send(int currentItem) {

            from =  prefs.getString("REG_FROM", "");
            Log.i(TAG,"from user   = " + from);

            Log.i(TAG,"Tab selected  = " + currentItem);
            //selectedTab = currentItem;
            wnConversation.setTab(currentItem);

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            //UUID messageGuid = UUID.randomUUID();
            //UUID convGuid = UUID.randomUUID();
            //wnConversation.setConversation_guid(convGuid.toString());

            WnMessageRowOptionFragment of = getVisibleFragment(wnConversation.getTab());
            selected_options = of.getSelectedOptions();
            Log.i(TAG, "selected_options = " + of.getSelectedOptions());


            WnMessage wnMessage = ObjectManager.createNewMessage( wnConversation.getContacts().get(0).getPhoneNumber(), selected_options, WnMessageStatus.SENT, 1) ;
            //wnMessage.setConversation_rowId(convGuid.toString());

            wnConversation.addMessage(wnMessage);

            /// fill parameters to the cloud gcm
            params = new ArrayList<>();
            params.add(new BasicNameValuePair("msg_id",wnMessage.getMessage_guid()));
            params.add(new BasicNameValuePair("fromu",from));
            params.add(new BasicNameValuePair("from_name","Me"));
            params.add(new BasicNameValuePair("to", (wnConversation.getContacts().get(0).getPhoneNumber()).replaceAll("[^0-9]", "")));
            params.add(new BasicNameValuePair("user_name", wnConversation.getContacts().get(0).getName()));
            params.add(new BasicNameValuePair("tab", "" + wnConversation.getTab()));
            params.add(new BasicNameValuePair("type", "" + wnConversation.getType()));
            params.add(new BasicNameValuePair("status", WnMessageStatus.NEW.toString()));
            params.add(new BasicNameValuePair("c_id", wnConversation.getConversation_guid()));
            params.add(new BasicNameValuePair("selected_options", selected_options));
            //MESSAGE SENDING
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/send", params);

            wnConversation.setStatus(WnMessageStatus.SENT);
            Long conversationRowId = ObjectManager.saveConversation(wnConversation);
            wnConversation.setRowId(conversationRowId);

            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            String res;
            try {


                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(), "The user has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bundle args = new Bundle();

                    Log.i(TAG, "new activit post exec c_id = " + wnConversation.getRowId());
                    args.putString("c_id", Long.toString(wnConversation.getRowId()));
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
