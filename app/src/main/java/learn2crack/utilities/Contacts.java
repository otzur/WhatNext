package learn2crack.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by otzur on 9/9/2015.
 */
public class Contacts {

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static Bitmap retrieveContactPhoto(Context context, String phoneNumber) {

        Bitmap photo = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactID = null;
        if(cursor.moveToFirst()) {
            contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            Log.i("WN", "contactID = " + contactID);
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        try {
            //String contactID = null;
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                Log.i("WN", "photo = " + photo.toString());
                //ImageView imageView = (ImageView) findViewById(R.id.img_contact);
                //imageView.setImageBitmap(photo);
            }
//            else
//            {
//                Log.i("WN", "photo is null");
//            }

            if( inputStream != null)
                inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }
}
