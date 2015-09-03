package learn2crack.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import learn2crack.adapters.FragmentAdapter;
import learn2crack.chat.R;
import learn2crack.db.ChatDataSource;

public class ResultActivity extends AppCompatActivity {

    private ChatFragment chatFragment;

    public ViewPager viewPager;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        context = getApplicationContext();
        ResultFragment resultFragment= new ResultFragment();
        resultFragment.setArguments(getIntent().getBundleExtra("INFO"));
        fragmentAdapter.addFragment(resultFragment, "Result Fragment");
        chatFragment = new ChatFragment();
        Bundle chatBundle = new Bundle();
        chatBundle.putString("c_id", getIntent().getBundleExtra("INFO").getString("c_id"));
        chatFragment.setArguments(chatBundle);
        fragmentAdapter.addFragment(chatFragment, "Chat fragment");
        viewPager.setAdapter(fragmentAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle newBundle = intent.getBundleExtra("INFO");
        Bundle currentBundle = getIntent().getBundleExtra("INFO");
        if(newBundle.getString("c_id","").equals(currentBundle.getString("c_id",""))){
            chatFragment.loadChatHistory(context);
        }
        else {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main22, menu);
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

    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                wnChatMessageReceiver, new IntentFilter("wn_chat_receiver"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                wnMessageReceiver, new IntentFilter("wn_message_receiver"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wnChatMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wnMessageReceiver);
    }

    private BroadcastReceiver wnChatMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getBundleExtra("INFO");
            Bundle currentBundle = getIntent().getBundleExtra("INFO");
            if(extra.getString("c_id","").equals(currentBundle.getString("c_id",""))){
                chatFragment.loadChatHistory(context);
            }/* else{
                Intent chat = new Intent(getApplicationContext(), ResultActivity.class);
                chat.putExtra("INFO", extra);
                chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(chat);
            }*/
        }
    };

    private BroadcastReceiver wnMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getBundleExtra("INFO");
            Bundle currentBundle = getIntent().getBundleExtra("INFO");
            if(extra.getString("c_id","").equals(currentBundle.getString("c_id",""))){
                setupViewPager(viewPager);
            }
            /*else{
                Intent chat = new Intent(getApplicationContext(), ResultActivity.class);
                chat.putExtra("INFO", extra);
                chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(chat);
            }*/
        }
    };
}
