package learn2crack.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
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
import learn2crack.chat.R;
import learn2crack.cheese.Cheeses;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageStatus;
import learn2crack.utilities.JSONParser;

/**
 * Created by otzur on 6/18/2015.
 */

public class WnMessageCustomActivity extends AppCompatActivity {


    Bundle bundle;
    SharedPreferences prefs;
    WnConversation wnConversation;
    List<NameValuePair> params;
    static final String TAG = "WN";
    EditText optionTextBox;
    SectionPagerAdapter sectionPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_custom_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        optionTextBox = (EditText) findViewById(R.id.option_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");

        try {
            if (bundle.getSerializable("conversation") != null) {

                wnConversation = (WnConversation) bundle.getSerializable("conversation");
            }
            else
            {
                throw new Exception("bundle.getSerializable(conversation) == null");
            }


        }
        catch (Exception ex){
            Log.e("WN","bundle.getSerializable(\"conversation\") throws exception- " + ex.getStackTrace());
            finish();
        }

        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        loadBackdrop();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Send(viewPager.getCurrentItem()).execute();
            }
        });

        FloatingActionButton fabAddOption = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(optionTextBox.getVisibility() != View.VISIBLE){
                    optionTextBox.setVisibility(View.VISIBLE);
                } else{
                    String tempText = optionTextBox.getText().toString();
                    if(!tempText.trim().equals("")){
                        ((WnMessageCustomOptionFragment)sectionPagerAdapter.getItem(0)).addOption(tempText);
                        optionTextBox.setText("");
                        sectionPagerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
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
            wnConversation.setTab(currentItem);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();

            WnMessageCustomOptionFragment of = getVisibleFragment(-1);
            selected_options = of.getSelectedOptions();
            Log.i(TAG, "selected_options = " + of.getSelectedOptions());


            WnMessage wnMessage = ObjectManager.createNewMessage( wnConversation.getContacts().get(0).getPhoneNumber(), selected_options, WnMessageStatus.SENT, 1) ;

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

            if(jObj == null){
                //handle send failed
                return null;
            }
            wnConversation.setStatus(WnMessageStatus.SENT);
            Long conversationRowId = ObjectManager.saveConversation(MainActivity.getAppContext(), wnConversation);
            wnConversation.setRowId(conversationRowId);

            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json == null){
                //handle send failed
                Toast.makeText(getApplicationContext(),"Failed to send Wn message, please try again in few moments",Toast.LENGTH_LONG);
                return;
            }
            String res;
            try {
                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(), "The user has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bundle args = new Bundle();
                    Log.i(TAG, "new activity post exec c_id = " + wnConversation.getRowId());
                    args.putSerializable("conversation", wnConversation);
                    //Intent chat = new Intent(getApplicationContext(), WnMessageDetailActivity.class);
                    //chat.putExtra("INFO", args);
                    //chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //getApplicationContext().startActivity(chat);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private WnMessageCustomOptionFragment getVisibleFragment(int index){
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if(fragments.isEmpty())
                return null;
            else
            {
                return (WnMessageCustomOptionFragment)fragments.get(index + 1);
            }
        }
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        WnMessageCustomOptionFragment mainFragment;

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(mainFragment!=null)
                return mainFragment;
            mainFragment = new WnMessageCustomOptionFragment();
            Bundle bundl = new Bundle();
            bundl.putInt("numberOfOptions", -1);
            mainFragment.setArguments(bundl);
            return mainFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                default:
                    return "Custom";
            }
        }
    }
}
