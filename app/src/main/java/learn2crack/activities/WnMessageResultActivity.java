package learn2crack.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.List;

import learn2crack.adapters.FragmentAdapter;
import learn2crack.chat.R;

public class WnMessageResultActivity extends AppCompatActivity {
    Bundle bundle;
    SharedPreferences prefs;
    List<NameValuePair> params;
    static final String TAG = "WN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_result_layout);
        Intent intent = getIntent();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        prefs = getSharedPreferences("Chat", 0);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        ResultFragment resultFragment= new ResultFragment();
        resultFragment.setArguments(getIntent().getBundleExtra("INFO"));
        fragmentAdapter.addFragment(resultFragment, "RESULT");
        viewPager.setAdapter(fragmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wn_message_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ResultPageAdapter extends FragmentPagerAdapter {
        public ResultPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {

            WnMessageResultRawFtagment wnMessageResultRawFragment = new WnMessageResultRawFtagment();
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
            wnMessageResultRawFragment.setArguments(bundl);
            return wnMessageResultRawFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
