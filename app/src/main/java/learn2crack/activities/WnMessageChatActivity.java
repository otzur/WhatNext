package learn2crack.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import learn2crack.adapters.ChatRVAdapter;
import learn2crack.adapters.ConversationRVAdapter;
import learn2crack.bl.ObjectManager;
import learn2crack.chat.R;
import learn2crack.db.ChatDataSource;
import learn2crack.db.ConversationDataSource;
import learn2crack.models.ChatMessage;
import learn2crack.models.WnChatMessage;
import learn2crack.models.WnContact;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessageStatus;
import learn2crack.utilities.Base64;
import learn2crack.utilities.JSONParser;

/**
 * Created by otzur on 10/14/2015.
 */
public class WnMessageChatActivity extends AppCompatActivity {


    static final String TAG = "WN/ChatActivity";
    private  WnConversation wnConversation;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<WnChatMessage> chatMessages;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private String myPhone;
    private EditText chatEditText;
    List<NameValuePair> params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_new_layout);

        rv = (RecyclerView) findViewById(R.id.rvOptionList);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        chatMessages = new ArrayList<>();
        String contactName = "TEST NAME";
        Bundle bundle = getIntent().getBundleExtra("INFO");
        myPhone= getSharedPreferences("Chat", 0).getString("REG_FROM","");
        wnConversation = (WnConversation) bundle.getSerializable("conversation");
        if(wnConversation != null){
            contactName = wnConversation.getContacts().get(0).getName();
            //wnConversation = (WnConversation) bundle.getSerializable("conversation");
            refreshChat();
        }
        chatEditText = (EditText)findViewById(R.id.chat_text);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(contactName);
        loadBackdrop();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = chatEditText.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                WnChatMessage chatMessage = ObjectManager.saveChatMessage(getApplicationContext(),wnConversation,
                        messageText, myPhone);
                chatMessage.setMe(true);
                chatMessages.add(chatMessages.size(),chatMessage);
                adapter.notifyDataSetChanged();
                new Send(messageText).execute();
                chatEditText.setText("");
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

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                wnChatMessageReceiver, new IntentFilter("wn_chat_receiver"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wnChatMessageReceiver);
    }

    private BroadcastReceiver wnChatMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getBundleExtra("INFO");
            //Bundle currentBundle = getIntent().getBundleExtra("INFO");
            WnConversation newWnConversation = (WnConversation) extra.getSerializable("conversation");
            if(newWnConversation.getConversation_guid().equals(wnConversation.getConversation_guid())){
                initializeData();
                adapter = new ChatRVAdapter(chatMessages, myPhone);
                rv.setAdapter(adapter);
            }
        }
    };

    private void refreshChat(){
        initializeData();
        adapter = new ChatRVAdapter(chatMessages, myPhone);
        rv.setAdapter(adapter);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setImageBitmap(wnConversation.getContacts().get(0).getPhoto());
        WnContact contact = wnConversation.getContacts().get(0);
        if(contact.getPhoto() != null) {
            Uri imageUri = getImageUri(getApplicationContext(), wnConversation.getContacts().get(0).getPhoto());
            Glide.with(this).load(imageUri).centerCrop().into(imageView);
        }
    }

    private void initializeData(){

        Cursor cursor = ObjectManager.getConversationChatMessages(this, wnConversation);
        int cursorCount = cursor.getCount();
        cursor.moveToFirst();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        for(int chatMessageID = 1 ; chatMessageID <= cursorCount ; chatMessageID++){
            WnChatMessage msg = new WnChatMessage();
            msg.setId(chatMessageID);
            msg.setFrom(cursor.getString(4));
            msg.setMe(msg.getFrom().equals(myPhone));
            msg.setChat_text(cursor.getString(3));
            msg.setDelivery_date(cursor.getString(1));
            chatMessages.add(msg);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private class Send extends AsyncTask<String, String, JSONObject> {

        ChatDataSource dba=new ChatDataSource(getApplicationContext());//Create this object in onCreate() method
        Bundle bundle;


        private String from;

        private WnMessageStatus status;
        private String deliveryTime;
        private String chatText;

        public Send(String message_text) {
            from = myPhone; //prefs.getString("REG_FROM","");
            Log.i(TAG,"from user   = " + from);
            status  = WnMessageStatus.CHAT;
            chatText = message_text;
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<>();
            params.add(new BasicNameValuePair("fromu",from));
            params.add(new BasicNameValuePair("to", (wnConversation.getContacts().get(0).getPhoneNumber()).replaceAll("[^0-9]", "")));
            params.add(new BasicNameValuePair("status", ""+ status));
            params.add(new BasicNameValuePair("c_id", wnConversation.getConversation_guid()));
            String str = new String(chatText.getBytes(), Charset.forName("UTF-8"));
            //try {
            //String ds= String.format("%040x", new BigInteger(1, str.getBytes(/*YOUR_CHARSET?*/)));
            String hexString="";
            try {
                byte[] bytes = {3 ,4};
                hexString = Base64.encode(chatText.getBytes());

            }
            catch (Exception e){

            }
            params.add(new BasicNameValuePair("text", hexString));//chatText.getBytes("UTF-8")));
            //}
            //catch (IOException ex){
            //    Log.e("WN",ex.getStackTrace().toString());
            //}
            /*.getBytes("UTF8").toString()*/

            //}

            //MESSAGE SENDING
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/sendchat", params);
            dba.open();
            WnChatMessage temp= dba.insert(chatText, from, wnConversation.getRowId());// Insert record in your DB
            dba.close();
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

                    /*datasource.open();
                    ((SimpleCursorAdapter) getListAdapter()).changeCursor(datasource.getAllDataByConversationRowID(conversation_rowId));
                    datasource.close();
                    ((SimpleCursorAdapter) getListAdapter()).notifyDataSetChanged();*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
