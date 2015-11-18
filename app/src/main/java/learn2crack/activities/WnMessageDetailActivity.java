package learn2crack.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.List;

import learn2crack.adapters.MessageDetailAdapter;
import learn2crack.bl.OptionSelectorManager;
import learn2crack.chat.R;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMatch;
import learn2crack.models.WnMessageResult;
import learn2crack.models.WnMessageRowOption;

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

        // Defined Array values to show in ListView
//        String[] values = new String[]
//                {
//                "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View"
//        };
//
//        int size = 0;
//        if(wnResult.getMatched() != null) {
//            size = wnResult.getMatched().size();
//        }
//        if(size > 0) {
//            mText.setText("Matched: \n");
//            for (int i = 0; i < size; i++) {
//                int index = Integer.valueOf(wnResult.getMatched().get(i));
//                mText.append(list.get(index).getName() + "\n");
//            }
//        }
//        else{
//            if(wnResult.isAllUsersResponded()) {
//                mText.setText("No match... not at all :( \n");
//                mText.setTextColor(Color.RED);
//            }
//            else{
//                mText.setText("Waiting for other user to respond \n");
//                mText.setTextColor(Color.BLUE);
//            }
//
//        }

        String contactName = "TEST NAME";
        Bundle bundle = getIntent().getBundleExtra("INFO");
        int numberOfOptions = 0;
        if(bundle.getSerializable("conversation") != null){

            wnConversation = (WnConversation) bundle.getSerializable("conversation");
            numberOfOptions = OptionSelectorManager.getNumberOfOptionsByTab(wnConversation.getTab());
            OptionSelectorManager optionSelectorManager = new OptionSelectorManager(numberOfOptions);
            List<WnMessageRowOption> list  = optionSelectorManager.getList();

            WnMessageResult wnMessageResult = wnConversation.getWnMessageResult();
            if(wnMessageResult.getWnMatch() == WnMatch.NO_MATCH)
                return;

           int size = 0;
           List<String> matched = wnMessageResult.getMatched();
           String[] values = null;
           if(wnMessageResult.getMatched() != null) {
                size = wnMessageResult.getMatched().size();

           }
           if(size > 0) {
                values = new String[size];
                //mText.setText("Matched: \n");
                for (int i = 0; i < size; i++) {
                    int index = Integer.valueOf(wnMessageResult.getMatched().get(i));
                    values[i] = list.get(index).getName();
                }
            }


            //////////////////////////////////////////////////////////////

            contactName = wnConversation.getContacts().get(0).getName();
            rvAdapter = new MessageDetailAdapter(values);
            rv.setAdapter(rvAdapter);

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
        imageView.setImageBitmap(wnConversation.getContacts().get(0).getPhoto());

        Uri imageUri  = getImageUri(getApplicationContext(), wnConversation.getContacts().get(0).getPhoto());
        Glide.with(this).load(imageUri).centerCrop().into(imageView);
        //Glide.with(this).load(wnConversation.getContacts().get(0).getPhoto()).centerCrop().into(imageView);
//        Glide.with(getApplicationContext())
//                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
//                .into(imageView);
    }

}

