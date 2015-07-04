package learn2crack.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

/**
 * Created by otzur on 6/18/2015.
 */

public class Message2Activity extends AppCompatActivity {

    Bundle bundle;
    //ImageButton btnSend;
    SharedPreferences prefs;
    List<NameValuePair> params;
    static final String TAG = "WN";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_tab_layout);


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

        if(bundle.getString("name") != null){

            //tvUserName.setText(bundle.getString("name"));
            collapsingToolbar.setTitle(bundle.getString("name"));
        }


//        btnSend.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                new Send(viewPager.getCurrentItem()).execute();
//                Toast.makeText( Message2Activity.this, "send  is clicked!", Toast.LENGTH_SHORT).show();
//            }
//
//        });

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        loadBackdrop();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Send(viewPager.getCurrentItem()).execute();
                Snackbar.make(view, "WN Message Sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();



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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        Log.i("WN", "inside on create Freind fregmant");
//        // Inflate the layout for this fragment
//
//
//        View view =  inflater.inflate(R.layout.message_tab_layout, container, false);
//
//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
//        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
//        TextView tvUserName = (TextView)view.findViewById(R.id.userName);
//        btnSend = (ImageButton) view.findViewById(R.id.btnSend);
//
//        prefs = getActivity().getSharedPreferences("Chat", 0);
//        bundle = getActivity().getIntent().getBundleExtra("INFO");
//
//        if(bundle.getString("name") != null){
//            tvUserName.setTextSize(20);
//            tvUserName.setText(bundle.getString("name"));
//        }
//
//
//        btnSend.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                new Send(viewPager.getCurrentItem()).execute();
//                Toast.makeText( getActivity(), "send  is clicked!", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//
//        viewPager.setAdapter(new SectionPagerAdapter(getActivity().getSupportFragmentManager()));
//        tabLayout.setupWithViewPager(viewPager);
//
//        return view;
//    }

        private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    private class Send extends AsyncTask<String, String, JSONObject> {

        MessageDB dba=new MessageDB(getApplicationContext());//Create this object in onCreate() method


        private int itemIndex;
        public Send(int currentItem) {
            Log.i("WN","send current item " + currentItem);
            itemIndex = currentItem;
                    }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("from", prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("fromn", prefs.getString("FROM_NAME", "")));
            params.add(new BasicNameValuePair("to", bundle.getString("mobno")));
            params.add((new BasicNameValuePair("msg","This is a test only")));
            int selectedTab= itemIndex;

            //int selectedTab= actionBar.getSelectedNavigationIndex();
            //if(mAdapter != null)
            //{
                OptionFragment of = getVisibleFragment(selectedTab);
                params.add((new BasicNameValuePair("SelectedOptions", of.getSelectedOptions())));
                Log.i(TAG, "SelectedOptions = " + of.getSelectedOptions());
            //}
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

            Log.i(TAG, "Going to save in database ");
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/send",params);

            dba.open();
            dba.insert(prefs.getString("REG_FROM", ""));// Insert record in your DB
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
                    //((MainActivity)getActivity()).changeToUserScreen();
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
            {
                return (OptionFragment)fragments.get(index + 1);
//            for(android.support.v4.app.Fragment fragment : fragments){
//
//                if(fragment != null && fragment.isVisible())
//
//                    Log.i(TAG, "fragment = " + fragment.getText(0).toString());
//                    return (OptionFragment) fragment;
//            }

            }
               // return (OptionFragment)fragments.get(index);
//            for(android.support.v4.app.Fragment fragment : fragments){
//                if(fragment != null && fragment.isVisible())
//                    return (OptionFragment) fragment;
//            }
            //return null;
        }
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            OptionFragment optionFragment = new OptionFragment();
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
            optionFragment.setArguments(bundl);
            return optionFragment;
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
