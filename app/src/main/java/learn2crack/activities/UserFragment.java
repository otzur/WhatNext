package learn2crack.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import learn2crack.bl.ObjectManager;
import learn2crack.chat.R;
import learn2crack.cheese.Cheeses;
import learn2crack.models.WnConversation;
import learn2crack.utilities.JSONParser;

import static android.provider.ContactsContract.RawContacts;

//import android.app.FragmentTransaction;


public class UserFragment extends Fragment {


    // ListView list;
    ArrayList<HashMap<String, String>> users;
    //ArrayList<String> userList = new ArrayList<>();

    Button refresh,logout;
    List<NameValuePair> params;
    SharedPreferences prefs;
    private ViewPager viewPager;


    private void restore(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            users = (ArrayList<HashMap<String,String>>) savedInstanceState.getSerializable("users");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("users", users);
    }



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        //users.clear();
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.user_fragment, container, false);

        //View view =inflater.inflate(R.layout.user_fragment, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);
        viewPager = (ViewPager) rv.findViewById(R.id.viewpager);




        try {
            if(users == null){
                users  = new ArrayList<>();
            }
            RecyclerView recyclerView= setupRecyclerView(rv);
            if(users.size() == 0)
            {
                Log.i("WN", "users list is null" );

                new Load(recyclerView).execute();
            }
            else {
                Log.i("WN", "users list is NOT null" );
                restore(savedInstanceState);
            }


        }
        catch(Exception e){
            int sds =23;
        }
        return rv;
        //list = (ListView)rv.findViewById(R.id.recyclerview);
        //refresh = (Button)rv.findViewById(R.id.refresh);
        //logout = (Button)rv.findViewById(R.id.logout);
        //logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new  Logout().execute();
//
//            }
//        });
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.viewpager)).commit();
//                Fragment reg = new UserFragment();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.viewpager, reg);
//                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
//                ft.addToBackStack(null);
//                ft.commit();
//
//            }
//        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private RecyclerView setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), userList));

//        ArrayList<HashMap<String, String>> users2 = (ArrayList<HashMap<String, String>>) users.clone();
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), users));
        return recyclerView;
    }


    private class Load extends AsyncTask<String, String, ArrayList<HashMap<String, String> > > {
        RecyclerView recyclerView;
        public Load(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... args) {
            JSONParser json = new JSONParser();

            params = new ArrayList<NameValuePair>();
            ArrayList<String> mContacts = new ArrayList<String>();
            Cursor cur = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

            String id = null, name = null;
            HashMap<String, String> map;
            Cursor pCur = null, temp = null;
            String MainNumber="";//phonetype=""
            boolean hasWN,toAdd;
            boolean forDebug = true;
            while (cur.moveToNext()) {
                hasWN = false;
                toAdd = false;
                map = new HashMap<String, String>();
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                MainNumber="";
                try {
                    String[] projection    = new String[] {
                            RawContacts._ID,  ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER , RawContacts.ACCOUNT_TYPE};
                    Cursor people = getActivity().getContentResolver().query(  ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,   RawContacts.CONTACT_ID + "= ?",
                            new String[] { id }, null);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int indexAccountType = people.getColumnIndex(RawContacts.ACCOUNT_TYPE);
                    int counter= 0;
                    while (people.moveToNext()){
                        String accountType = people.getString(indexAccountType);
                        MainNumber = people.getString(indexNumber);
                        if(accountType != null && accountType.equals("learn2crack.chat.account")){
                            hasWN = true;

                            break;
                        }
                    }
                    if ((!name.equals("")) && (!MainNumber.equals(""))){
                        toAdd = true;
                        map.put("name", name);
                        //userList.add(name);
                        map.put("mobno", MainNumber);
                        if(hasWN){
                            map.put("wn", "true");
                        }
                        else {
                            map.put("wn", "false");
                        }
                    }
                    if(toAdd) {
                        users.add(map);
                    }
                }
                catch (Exception e){

                }

                if(pCur == null){

                }
            }
            if(pCur != null) {
                pCur.close();
            }
            if(cur != null){
                cur.close();
            }
            //phones.close();
            return users;
            //JSONObject obj=new arrayLToJSON(users);
            //return  arrayLToJSON(users);
        }
        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> res) {
            recyclerView.getAdapter().notifyDataSetChanged();
            /*for(int i = 0; i < json.length(); i++){
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    String name = c.getString("name");
                    String mobno = c.getString("mobno");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("mobno", mobno);
                    users.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }*/

//            ListAdapter adapter = new UserAdapter(getActivity(), users,
//                    R.layout.user_list_single,
//                    new String[] { "name","mobno" , "wn" }, new int[] {
//                    R.id.name, R.id.mobno , R.id.micon});
//            list.setAdapter(adapter);
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int position, long id) {
//
//                    if(users.get(position).get("wn").equals("true")) {
//                        Bundle args = new Bundle();
//                        args.putString("mobno", users.get(position).get("mobno"));
//                        args.putString("name", users.get(position).get("name"));
//                        Intent chat = new Intent(getActivity(), Message2Activity.class);
//                        chat.putExtra("INFO", args);
//                        startActivity(chat);
//                    }
//                }
//            });
        }
    }

    private class Logout extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            Log.i("WN", "Logout clicked");
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/logout",params);

            Log.i("WN", "Logout sent");
            //Toast.makeText(this, "Logout is clicked!", Toast.LENGTH_SHORT).show();

            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String res = null;
            try {
                res = json.getString("response");
                Toast.makeText(getActivity(),res,Toast.LENGTH_SHORT).show();
                if(res.equals("Removed Successfully")) {
                    Fragment reg = new LoginFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.viewpager, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                    ft.addToBackStack(null);
                    ft.commit();

                    ////////////////
//                    Fragment login = new LoginFragment();
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.container_body, login);
//                    fragmentTransaction.commit();

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", "");
                    edit.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        //private List<String>  mValues;
        private List<HashMap<String, String>> mValues;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;
            public final TextView mTextView2;
            public final ImageView mImgWn;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mTextView2 = (TextView) view.findViewById(android.R.id.text2);
                mImgWn = (ImageView)view.findViewById(R.id.micon);

            }

            @Override
            public String toString() {

                return super.toString() + " '" + mTextView.getText();
            }
        }

        public String getValueAt(int position) {

            return mValues.get(position).get(0);
        }


        public SimpleStringRecyclerViewAdapter(Context context, List<HashMap<String, String>> items) {
            //public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_list_single, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            HashMap<String,String> item =  mValues.get(position);
            holder.mBoundString = item.toString();
            holder.mTextView.setText(item.get("name"));
            holder.mTextView2.setText(item.get("mobno"));

            if(item.get("wn").equals("true")){
                holder.mImgWn.setVisibility(View.VISIBLE);
            }
            else{
                holder.mImgWn.setVisibility(View.INVISIBLE);
            }
            //holder.mTextView2.setVisibility();
//            holder.mBoundString = mValues.get(position);
//            holder.mTextView.setText(mValues.get(position));
//            holder.mTextView2.setText(mValues.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    TextView text1 = (TextView) v.findViewById(android.R.id.text1);
                    TextView text2= (TextView) v.findViewById(android.R.id.text2);

                    Log.i("WN", "text1 = " + text1.getText().toString());
                    Log.i("WN", "text2 = " + text2.getText().toString());
                    //Intent intent = new Intent(context, CheeseDetailActivity.class);
                    //intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);

//                    if(users.get(position).get("wn").equals("true")) {
                    //String name = (String) text1.getText();
                    String mobno = (String) text2.getText();
                    String type = "HimAndHer";
                    String status = "New";

                    WnConversation wnConversation =  ObjectManager.createNewConversation(mobno,type, status);
                    Bundle args = new Bundle();
                    args.putSerializable("conversation", wnConversation);

//                    args.putString("mobno", (String) text2.getText());
//                    args.putString("name", (String) text1.getText());
//                    args.putString("type", "HimAndHer");
//                    args.putString("status", "New");
                    Intent newMessageIntent = new Intent(context , WnMessageNewActivity.class);
                    newMessageIntent.putExtra("INFO", args);
                    context.startActivity(newMessageIntent);

//                        Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());
//                        adapter.addFragment(new Message2Activity(), "Message");
//                        viewPager.setAdapter(adapter);

//                        Fragment reg = new FriendsFragment();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.replace(R.id.viewpager, reg);
//                        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
//                        ft.addToBackStack(null);
//                        ft.commit();

//                    }


                    ////////

                    //context.startActivity(intent);
                }
            });

            Glide.with(holder.mImageView.getContext())
                    .load(Cheeses.getRandomCheeseDrawable())
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}