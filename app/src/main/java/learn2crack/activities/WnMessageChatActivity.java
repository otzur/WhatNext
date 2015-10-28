package learn2crack.activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import learn2crack.adapters.ChatRVAdapter;
import learn2crack.adapters.ConversationRVAdapter;
import learn2crack.bl.ObjectManager;
import learn2crack.chat.R;
import learn2crack.db.ConversationDataSource;
import learn2crack.models.ChatMessage;
import learn2crack.models.WnChatMessage;
import learn2crack.models.WnConversation;

/**
 * Created by otzur on 10/14/2015.
 */
public class WnMessageChatActivity extends AppCompatActivity {


    static final String TAG = "WN/WnMessageDetailActivity";
    private  WnConversation wnConversation;
    ListView lvResult;
    private List<ChatMessage> chatMessages;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_new_layout);

        // Get ListView object from xml
        //lvResult = (ListView) findViewById(R.id.listResult);

        String contactName = "TEST NAME";
        Bundle bundle = getIntent().getBundleExtra("INFO");

        if(bundle.getSerializable("conversation") != null){

            wnConversation = (WnConversation) bundle.getSerializable("conversation");
            contactName = wnConversation.getContacts().get(0).getName();
        }


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(contactName);

        loadBackdrop();

        // Defined Array values to show in ListView
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        setAdapter();

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
       // lvResult.setAdapter(adapter);
    }

    private void setAdapter(){
        Log.i("WN", "inside set adapter in chat activity");
        rv = (RecyclerView) inflater.inflate( R.layout.message_recyclerview, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
    }

    private void initializeData(){

        Cursor cursor = ObjectManager.getConversationChatMessages(this, wnConversation);
        int cursorCount = cursor.getCount();
        cursor.moveToFirst();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        for(int chatMessageID = 1 ; chatMessageID <= cursorCount ; chatMessageID++){
            WnChatMessage msg = new WnChatMessage();
            msg.setId(chatMessageID);
            msg.setMe(cursor.getString(4).equals(myPhone));
            msg.
            msg.setMessage(cursor.getString(3));
            //msg.setDate(DateFormat.getDateTimeInstance().format(cursor.getString(1)));
            msg.setDate(cursor.getString(1));
            chatMessages.add(msg);
            cursor.moveToNext();
        }
        cursor.close();
        adapter = new ChatRVAdapter(chatMessages, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
    }

    private void initializeAdapter() {

        adapter = new ConversationRVAdapter(conversations);
        rv.setAdapter(adapter);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void loadBackdrop() {
        Bitmap contactPhoto= wnConversation.getContacts().get(0).getPhoto();
        if(contactPhoto == null){
            return;
        }
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setImageBitmap(wnConversation.getContacts().get(0).getPhoto());

        Uri imageUri  = getImageUri(getApplicationContext(), wnConversation.getContacts().get(0).getPhoto());
        Glide.with(this).load(imageUri).centerCrop().into(imageView);
    }

}
