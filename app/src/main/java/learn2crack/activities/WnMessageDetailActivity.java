package learn2crack.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import learn2crack.chat.R;
import learn2crack.models.WnConversation;

/**
 * Created by otzur on 10/14/2015.
 */
public class WnMessageDetailActivity extends AppCompatActivity {


    static final String TAG = "WN/WnMessageDetailActivity";
    private  WnConversation wnConversation;
    ListView lvResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_layout);

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
//
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
