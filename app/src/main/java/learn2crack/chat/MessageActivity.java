package learn2crack.chat;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by otzur on 6/3/2015.
 */
public class MessageActivity extends FragmentActivity implements  ActionBar.TabListener {


    /** Called when the activity is first created. */
    Bundle bundle;
    ImageButton btnSend;
    SharedPreferences prefs;
    List<NameValuePair> params;
    static final String TAG = "WN";

    private ViewPager viewPager;
    private TabsPagerAdapter  mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Coin", "Roulette", "Table" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wn_message2);

        TextView tvUserName = (TextView)findViewById(R.id.userName);
        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");

        if(bundle.getString("name") != null){
            tvUserName.setTextSize(20);
            tvUserName.setText(bundle.getString("name"));
        }

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new Send().execute();
                Toast.makeText(MessageActivity.this,"send  is clicked!", Toast.LENGTH_SHORT).show();

            }

        });

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Resources resources = getResources();
        Bundle bundl = new Bundle();
        // Adding Tabs
        for (String tab_name : tabs) {

            ActionBar.Tab t = actionBar.newTab();
            t.setText(tab_name);
            t.setTabListener(this);
            if (tab_name.equals("Coin")) {

                bundl.putInt("numberOfOptions",2);
                t.setTag(2);
            }
            else if(tab_name.equals("Roulette")) {
                bundl.putInt("numberOfOptions",5);
                t.setTag(5);
            }
            else if(tab_name.equals("Table")) {
                bundl.putInt("numberOfOptions",8);
                t.setTag(8);

            }

            //t.setIcon(resources.getDrawable(R.drawable.table));
            actionBar.addTab(t);

        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {


    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private class Send extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("from", prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("fromn", prefs.getString("FROM_NAME", "")));
            params.add(new BasicNameValuePair("to", bundle.getString("mobno")));
            params.add((new BasicNameValuePair("msg","This is a test only")));
            int selectedTab= actionBar.getSelectedNavigationIndex();
            if(mAdapter != null)
            {
                OptionFragment of = getVisibleFragment(selectedTab);
                params.add((new BasicNameValuePair("SelectedOptions", of.getSelectedOptions())));
                Log.i(TAG, "SelectedOptions = " +  of.getSelectedOptions());
            }
            //actionBar.Tab tempTab= actionBar.getTabAt(selectedTab);
            //tempTab
            //params.add(new BasicNameValuePair("msgType",getTabHost().getCurrentTabTag()));

            //View view  = getTabHost().getCurrentTab().getCurrentTabView();



//            String tabTag = getTabHost().getCurrentTabTag();
//            Option2Activity activity = (Option2Activity)getLocalActivityManager().getActivity(tabTag);
//            // ArrayList<int> selected = activity.getSelectedOptions();
//            String selected = activity.getSelectedOptions();
//
//            Log.i(TAG, selected);

            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/send",params);
            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            //chat_msg.setText("");

            String res = null;
            try {
                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(), "The user has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private OptionFragment getVisibleFragment(int index){
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if(fragments.isEmpty())
                return null;
            else
                return (OptionFragment)fragments.get(index);
//            for(android.support.v4.app.Fragment fragment : fragments){
//                if(fragment != null && fragment.isVisible())
//                    return (OptionFragment) fragment;
//            }
//            return null;
        }
    }
}
