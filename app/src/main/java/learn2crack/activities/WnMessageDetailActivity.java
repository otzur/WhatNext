package learn2crack.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.List;

import learn2crack.adapters.MessageDetailAdapter;
import learn2crack.bl.OptionSelectorManager;
import learn2crack.chat.R;
import learn2crack.models.WnContact;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMatch;
import learn2crack.models.WnMessageResult;
import learn2crack.models.WnMessageRowOption;
import learn2crack.models.WnMessageStatus;

/**
 * Created by otzur on 10/14/2015.
 */
public class WnMessageDetailActivity extends AppCompatActivity {


    static final String TAG = "WN/WnMessageDetailActivity";
    private  WnConversation wnConversation;
    private RecyclerView rv;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_layout);


        rv = (RecyclerView) findViewById(R.id.rvOptionList);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        String contactName = "TEST NAME";
        Bundle bundle = getIntent().getBundleExtra("INFO");
        int numberOfOptions = 0;
        //sets the adapter and load the results
        if(bundle.getSerializable("conversation") != null){
            wnConversation = (WnConversation) bundle.getSerializable("conversation");
            contactName = wnConversation.getContacts().get(0).getName();
            refreshResults();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(contactName);

        //Load contact image
        loadBackdrop();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("conversation", wnConversation);
                Intent intent = new Intent(getApplicationContext(), WnMessageChatActivity.class);
                intent.putExtra("INFO", args);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        WnContact contact = wnConversation.getContacts().get(0);
        if(contact.getPhoto() != null) {
            imageView.setImageBitmap(wnConversation.getContacts().get(0).getPhoto());
            Uri imageUri = getImageUri(getApplicationContext(), wnConversation.getContacts().get(0).getPhoto());
            Glide.with(this).load(imageUri).centerCrop().into(imageView);
        }
    }

    private void refreshResults(){
        int numberOfOptions = OptionSelectorManager.getNumberOfOptionsByTab(wnConversation.getTab());
        OptionSelectorManager optionSelectorManager = new OptionSelectorManager(numberOfOptions);
        List<WnMessageRowOption> list  = optionSelectorManager.getList();
        String[] values = null;
        if(wnConversation.getStatus().equals(WnMessageStatus.RESULTS)) {
            WnMessageResult wnMessageResult = wnConversation.getWnMessageResult();
            if (wnMessageResult.getWnMatch() == WnMatch.NO_MATCH) {
                values = new String[1];
                values[0] = "No match";
            }
            else {
                int size = 0;
                if (wnMessageResult.getMatched() != null) {
                    size = wnMessageResult.getMatched().size();

                }
                if (size > 0) {
                    values = new String[size];
                    for (int i = 0; i < size; i++) {
                        int index = Integer.valueOf(wnMessageResult.getMatched().get(i));
                        values[i] = list.get(index).getName();
                    }
                }
            }
           /* else{
                values = new String[1];
                values[0] = "No match";
            }*/
        }
        else{
            values = new String[1];
            values[0] = "Waiting for " + wnConversation.getContacts().get(0).getName() + " to respond";
        }
        rvAdapter = new MessageDetailAdapter(values);
        rv.setAdapter(rvAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filters = new IntentFilter("wn_message_receiver");
        filters.addAction("wn_reveal_receiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                wnMessageReceiver, filters);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wnMessageReceiver);
    }

    private BroadcastReceiver wnMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getBundleExtra("INFO");
            WnConversation newWnConversation = (WnConversation) bundle.getSerializable("conversation");
            if(newWnConversation != null){
                if(wnConversation.getConversation_guid().equals(newWnConversation.getConversation_guid())){
                    if(action.equals("wn_message_receiver")) {
                        wnConversation = newWnConversation;
                        refreshResults();
                    }
                    else{
                        //handle reveal action that refers for this wn conversation
                        Toast.makeText(context, "reveal request received", Toast.LENGTH_LONG);
                    }
                }
            }
        }
    };

}

