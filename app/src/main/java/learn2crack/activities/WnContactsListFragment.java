/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package learn2crack.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import learn2crack.chat.R;
import learn2crack.cotacts.Contact;
import learn2crack.utilities.ImageLoader;
import learn2crack.utilities.Utils;


public class WnContactsListFragment extends ListFragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    ArrayList<Contact> list = new ArrayList<Contact>();
    LongSparseArray<Contact> array = new LongSparseArray<Contact>();

    private ArrayAdapter<Contact> mAdapter; // The main query adapter
    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread
    // Contact selected listener that allows the activity holding this fragment to be notified of
    // a contact being selected
    private OnContactsInteractionListener mOnContactSelectedListener;
    /**
     * Fragments require an empty constructor.
     */
    public WnContactsListFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new ContactsAdapter(getActivity(),(ArrayList<Contact>)list);
        mImageLoader = new ImageLoader(getActivity(), getListPreferredItemHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                if(data instanceof Integer){
                    return loadContactPhotoThumbnail(String.valueOf(data) , getImageSize());
                }
                return loadContactPhotoThumbnail((String) data, getImageSize());
            }
        };

        // Set a placeholder loading image for the image loader
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);
        // Add a cache to the image loader
        mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the list fragment layout
        return inflater.inflate(R.layout.contact_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up ListView, assign adapter and set some listeners. The adapter was previously
        // created in onCreate().
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageLoader.setPauseWork(true);
                } else {
                    mImageLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
        getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Assign callback listener which the holding activity must implement. This is used
            // so that when a contact item is interacted with (selected by the user) the holding
            // activity will be notified and can take further action such as populating the contact
            // detail pane (if in multi-pane layout) or starting a new activity with the contact
            // details (single pane layout).
            mOnContactSelectedListener = (OnContactsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactsInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // In the case onPause() is called during a fling the image loader is
        // un-paused to let any remaining background work complete.
        mImageLoader.setPauseWork(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // Notifies the parent activity that the user selected a contact. In a two-pane layout, the
        // parent activity loads a ContactDetailFragment that displays the details for the selected
        // contact. In a single-pane layout, the parent activity starts a new activity that
        // displays contact details in its own Fragment.
        mOnContactSelectedListener.onContactSelected(null);

        registerForContextMenu(parent);
        //parent.setTag(R.id.TAG_HAS_WN,v.getTag(R.id.TAG_HAS_WN));
        parent.setTag(R.id.TAG_CONTACT_ID, v.getTag(R.id.TAG_CONTACT_ID));
        parent.setTag(R.id.TAG_CONTACT, v.getTag(R.id.TAG_CONTACT));
        parent.showContextMenu();
        unregisterForContextMenu(parent);
    }

    /**
     * Called when ListView selection is cleared, for example
     * when search mode is finished and the currently selected
     * contact should no longer be selected.
     */
    private void onSelectionCleared() {
        // Uses callback to notify activity this contains this fragment
        mOnContactSelectedListener.onSelectionCleared();

        // Clears currently checked item
        getListView().clearChoices();
    }

    // This method uses APIs from newer OS versions than the minimum that this app supports. This
    // annotation tells Android lint that they are properly guarded so they won't run on older OS
    // versions and can be ignored by lint.
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // In version 3.0 and later, sets up and configures the ActionBar SearchView
        if (Utils.hasHoneycomb()) {

            // Retrieves the system search manager service
            final SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Returns a new CursorLoader for querying the Contacts table. No arguments are used
            // for the selection clause. The search string is either encoded onto the content URI,
            // or no contacts search string is used. The other search criteria are constants. See
            // the ContactsQuery interface.
            return new CursorLoader(getActivity(),
                    ContactsQuery.CONTENT_URI,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    ContactsQuery.selectionArgs,
                    ContactsQuery.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This swaps the new cursor into the adapter.
        while (data.moveToNext()) {
            long id = data.getLong(ContactsQuery.idIdx);
            Contact currentContact = array.get(id);
            if (currentContact == null) {
                if(!(data.getString(ContactsQuery.accountTypeIdx)).equals("learn2crack.chat.account"))
                {
                    continue;
                }
                currentContact = new Contact(id, data.getString(ContactsQuery.nameIdx), getResources());
                array.put(id, currentContact);
                list.add(currentContact);
            }
            int type = data.getInt(ContactsQuery.typeIdx);
            String mimeType = data.getString(ContactsQuery.mimeTypeIdx);
            if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                // mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                String phone = data.getString(ContactsQuery.dataIdx);
                currentContact.addPhone(type, phone);
            } else {
                // mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                String photo = data.getString(ContactsQuery.dataIdx);
                //currentContact.SetPhoto(BitmapFactory.decodeByteArray(photo,0,photo.length));
            }
        }
        data.close();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Gets the preferred height for each item in the ListView, in pixels, after accounting for
     * screen density. ImageLoader uses this value to resize thumbnail images to match the ListView
     * item height.
     *
     * @return The preferred height in pixels, based on the current theme.
     */
    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();

        // Resolve list item preferred height theme attribute into typedValue
        getActivity().getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);

        // Create a new DisplayMetrics object
        final DisplayMetrics metrics = new DisplayMetrics();

        // Populate the DisplayMetrics
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Return theme value based on DisplayMetrics
        return (int) typedValue.getDimension(metrics);
    }

    /**
     * Decodes and scales a contact's image from a file pointed to by a Uri in the contact's data,
     * and returns the result as a Bitmap. The column that contains the Uri varies according to the
     * platform version.
     *
     * @param photoData For platforms prior to Android 3.0, provide the Contact._ID column value.
     *                  For Android 3.0 and later, provide the Contact.PHOTO_THUMBNAIL_URI value.
     * @param imageSize The desired target width and height of the output image in pixels.
     * @return A Bitmap containing the contact's image, resized to fit the provided image size. If
     * no thumbnail exists, returns null.
     */
    private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

        // Ensures the Fragment is still added to an activity. As this method is called in a
        // background thread, there's the possibility the Fragment is no longer attached and
        // added to an activity. If so, no need to spend resources loading the contact photo.
        if (!isAdded() || getActivity() == null) {
            return null;
        }

        // Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
        // ContentResolver can return an AssetFileDescriptor for the file.
        AssetFileDescriptor afd = null;

        // This "try" block catches an Exception if the file descriptor returned from the Contacts
        // Provider doesn't point to an existing file.
        try {
            Uri thumbUri;
            // If Android 3.0 or later, converts the Uri passed as a string to a Uri object.
            if (Utils.hasHoneycomb()) {
                thumbUri = Uri.parse(photoData);
            } else {
                // For versions prior to Android 3.0, appends the string argument to the content
                // Uri for the Contacts table.
                final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);

                // Appends the content Uri for the Contacts.Photo table to the previously
                // constructed contact Uri to yield a content URI for the thumbnail image
                thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
            }
            // Retrieves a file descriptor from the Contacts Provider. To learn more about this
            // feature, read the reference documentation for
            // ContentResolver#openAssetFileDescriptor.
            afd = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");

            // Gets a FileDescriptor from the AssetFileDescriptor. A BitmapFactory object can
            // decode the contents of a file pointed to by a FileDescriptor into a Bitmap.
            FileDescriptor fileDescriptor = afd.getFileDescriptor();

            if (fileDescriptor != null) {
                // Decodes a Bitmap from the image pointed to by the FileDescriptor, and scales it
                // to the specified width and height
                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        fileDescriptor, imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {

        } finally {
            // If an AssetFileDescriptor was returned, try to close it
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                    // Closing a file descriptor might cause an IOException if the file is
                    // already closed. Nothing extra is needed to handle this.
                }
            }
        }

        // If the decoding failed, returns null
        return null;
    }

    /**
     * This interface must be implemented by any activity that loads this fragment. When an
     * interaction occurs, such as touching an item from the ListView, these callbacks will
     * be invoked to communicate the event back to the activity.
     */
    public interface OnContactsInteractionListener {
        /**
         * Called when a contact is selected from the ListView.
         * @param contactUri The contact Uri.
         */
        public void onContactSelected(Uri contactUri);

        /**
         * Called when the ListView selection is cleared like when
         * a contact search is taking place or is finishing.
         */
        public void onSelectionCleared();
    }

    /**
     * This interface defines constants for the Cursor and CursorLoader, based on constants defined
     * in the {@link Contacts} class.
     */
    public interface ContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;

        // The selection clause for the CursorLoader query. The search criteria defined here
        // restrict results to contacts that have a display name and are linked to visible groups.
        // Notice that the search on the string provided by the user is implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION = ContactsContract.Data.MIMETYPE + " in (?, ?)";

        @SuppressLint("InlinedApi")
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };

        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
        // sort key allows for localization. In earlier versions. use the display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER = Contacts.SORT_KEY_ALTERNATIVE;

        // The projection for the CursorLoader query. This is a list of columns that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
                ContactsContract.CommonDataKinds.Contactables.ACCOUNT_TYPE_AND_DATA_SET,
        };

        // The query column numbers which map to each value in the projection
        final static int mimeTypeIdx  = 0;
        final static int idIdx  = 1;
        final static int nameIdx  = 2;
        final static int dataIdx  = 3;
        final static int typeIdx  = 4;
        final static int accountTypeIdx = 5;
    }

    private class ContactsAdapter extends ArrayAdapter<Contact> implements SectionIndexer {
        private LayoutInflater mInflater; // Stores the layout inflater
        private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
        private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style

        /**
         * Instantiates a new Contacts Adapter.
         * @param context A context that has access to the app's layout.
         */
        public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
            super(context, 0, contacts);

            // Stores inflater for use later
            mInflater = LayoutInflater.from(context);

            // Loads a string containing the English alphabet. To fully localize the app, provide a
            // strings.xml file in res/values-<x> directories, where <x> is a locale. In the file,
            // define a string with android:name="alphabet" and contents set to all of the
            // alphabetic characters in the language in their proper sort order, in upper case if
            // applicable.
            final String alphabet = context.getString(R.string.alphabet);

            // Instantiates a new AlphabetIndexer bound to the column used to sort contact names.
            // The cursor is left null, because it has not yet been retrieved.
            //mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);

            // Defines a span for highlighting the part of a display name that matches the search
            // string
            highlightTextSpan = new TextAppearanceSpan(getActivity(), R.style.searchTextHiglight);
        }


        /**
         * Overrides newView() to inflate the list item views.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the list item layout.
            final View itemLayout =
                    mInflater.inflate(R.layout.contact_list_item, parent, false);

            // Creates a new ViewHolder in which to store handles to each view resource. This
            // allows bindView() to retrieve stored references instead of calling findViewById for
            // each instance of the layout.
            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
            holder.icon = (ImageView) itemLayout.findViewById(android.R.id.icon);
            itemLayout.setTag(holder);
            // Returns the item layout view
            bindView(position, itemLayout);
            return itemLayout;
        }

        /**
         * Binds data from the Cursor to the provided view.
         */
        public void bindView(int position, View v)  {
            // Gets handles to individual view resources
            final ViewHolder holder = (ViewHolder) v.getTag();
            String phoneNumber="";
            Contact contact= list.get(position);
            v.setTag(R.id.TAG_CONTACT_ID,(contact.getId()));
            v.setTag(R.id.TAG_CONTACT, contact);
            // For Android 3.0 and later, gets the thumbnail image Uri from the current Cursor row.
            // For platforms earlier than 3.0, this isn't necessary, because the thumbnail is
            // generated from the other fields in the row.
            String photoUri = contact.getPhotoURI();

            final String displayName = contact.getName();

            holder.text1.setText(displayName);
            holder.text2.setVisibility(View.GONE);

            // Processes the QuickContactBadge. A QuickContactBadge first appears as a contact's
            // thumbnail image with styling that indicates it can be touched for additional
            // information. When the user clicks the image, the badge expands into a dialog box
            // containing the contact's details and icons for the built-in apps that can handle
            // each detail type.


            // Binds the contact's lookup Uri to the QuickContactBadge
            //holder.icon.assignContactUri(contactUri);
            //holder.icon.setMode(ContactsContract.QuickContact.MODE_SMALL);

            // Loads the thumbnail image pointed to by photoUri into the QuickContactBadge in a
            // background worker thread
            if(photoUri.equals("")){
                photoUri= "android.resource://learn2crack.activities" + R.mipmap.ic_launcher;
            }
            mImageLoader.loadImage(photoUri, holder.icon);

        }

        /**
         * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
         * CursorAdapter.
         */
        /*@Override
        public Cursor swapCursor(Cursor newCursor) {
            // Update the AlphabetIndexer with new cursor as well
            mAlphabetIndexer.setCursor(newCursor);
            return super.swapCursor(newCursor);
        }*/

        /**
         * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
         * getCount returns zero. As a result, no test for Cursor == null is needed.
         */
        @Override
        public int getCount() {
            if (array == null) {
                return 0;
            }
            return array.size();
        }

        /**
         * Defines the SectionIndexer.getSections() interface.
         */
        @Override
        public Object[] getSections() {
            return null;//mAlphabetIndexer.getSections();
        }

        /**
         * Defines the SectionIndexer.getPositionForSection() interface.
         */
        @Override
        public int getPositionForSection(int i) {
            if (array == null) {
                return 0;
            }
            return 0;//mAlphabetIndexer.getPositionForSection(i);
        }

        /**
         * Defines the SectionIndexer.getSectionForPosition() interface.
         */
        @Override
        public int getSectionForPosition(int i) {
            if (array == null) {
                return 0;
            }
            return 0;//mAlphabetIndexer.getSectionForPosition(i);
        }

        /**
         * A class that defines fields for each resource ID in the list item layout. This allows
         * ContactsAdapter.newView() to store the IDs once, when it inflates the layout, instead of
         * calling findViewById in each iteration of bindView.
         */
        private class ViewHolder {
            TextView text1;
            TextView text2;
            ImageView icon;
        }
    }
}
