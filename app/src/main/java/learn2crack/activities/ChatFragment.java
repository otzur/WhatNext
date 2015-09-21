package learn2crack.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import learn2crack.adapters.ChatAdapter;
import learn2crack.chat.R;
import learn2crack.db.ChatDataSource;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.DatabaseHelper;
import learn2crack.models.ChatMessage;
import learn2crack.models.WnChatMessage;
import learn2crack.models.WnConversation;
import learn2crack.utilities.JSONParser;


public class ChatFragment extends ListFragment {

    private DatabaseHelper DBHelper;
    static final String TAG = "WN";
    private SharedPreferences prefs;
    List<NameValuePair> params;
    private String conversation_rowId, conversation_guid;
    private EditText chatTextView;
    private String to_user;
    private ChatDataSource datasource;


    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String myPhone;

    private int chatMessageID;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.conversation_rowId = args.getString("c_id");
    }

    private void initControls(View view) {
        messagesContainer = (ListView) view.findViewById(android.R.id.list);
        messagesContainer.setItemsCanFocus(false);
        messageET = (EditText) view.findViewById(R.id.messageEdit);
        sendBtn = (Button) view.findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) view.findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) view.findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
        companionLabel.setText("My Buddy");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessageID++;
                chatMessage.setId(chatMessageID);
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");
                displayMessage(chatMessage);
                new Send(messageText).execute();
            }
        });


    }

    public void loadChatHistory(Context context){

        chatHistory = new ArrayList<ChatMessage>();

        datasource = new ChatDataSource(context);
        ConversationDataSource conversationDataSource = new ConversationDataSource(context);
        conversationDataSource.open();
        WnConversation conversation = conversationDataSource.getConversationByRowID(conversation_rowId);
        conversation_guid = conversation.getConversation_guid();
        conversationDataSource.close();
        datasource.open();
        Cursor cursor = datasource.getAllDataByConversationRowID(conversation_rowId);
        int cursorCount = cursor.getCount();
        cursor.moveToFirst();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        for(chatMessageID = 1 ; chatMessageID <= cursorCount ; chatMessageID++){
            ChatMessage msg = new ChatMessage();
            msg.setId(chatMessageID);
            msg.setMe(cursor.getString(4).equals(myPhone));
            msg.setMessage(cursor.getString(3));
            //msg.setDate(DateFormat.getDateTimeInstance().format(cursor.getString(1)));
            msg.setDate(cursor.getString(1));
            chatHistory.add(msg);
            cursor.moveToNext();
        }
        cursor.close();
        adapter = new ChatAdapter(getActivity(), new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view =  inflater.inflate(R.layout.fragment_result_chat, container, false);
        View view =  inflater.inflate(R.layout.activity_chat, container, false);
        initControls(view);
        prefs = view.getContext().getSharedPreferences("Chat", 0);
        //chatTextView = (EditText)view.findViewById(R.id.message_text);
        ConversationDataSource conversationDataSource = new ConversationDataSource(getActivity());
        conversationDataSource.open();
        Set<String> phones= conversationDataSource.getUsersPhones(Long.valueOf(conversation_rowId));
        myPhone= prefs.getString("REG_FROM","");
        conversationDataSource.close();
        phones.remove(myPhone);
        to_user = (phones.toArray())[0].toString().trim();
        //String check="";
        for(int i = 0 ; i<phones.size(); i++){
            if(to_user.equals("") && !((phones.toArray())[i]).equals("")){
                to_user = (phones.toArray()[i]).toString();
            }
            //check+=((phones.toArray())[i])+"  ";
        }
        datasource = new ChatDataSource(view.getContext());
        ConversationDataSource conversationDataSource1 = new ConversationDataSource(view.getContext());
        conversationDataSource.open();
        WnConversation conversation = conversationDataSource.getConversationByRowID(conversation_rowId);
        conversation_guid = conversation.getConversation_guid();
        conversationDataSource.close();
        /*datasource.open();
        Cursor cursor = datasource.getAllDataByConversationRowID(conversation_rowId);
        String[] from = new String[] { DBHelper.KEY_CHAT_FROM, DBHelper.KEY_CHAT_DELIVERY ,DBHelper.KEY_CHAT_TEXT};
        int[] to = new int[] {R.id.from_user, R.id.delivery,  R.id.message_text};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(view.getContext(), R.layout.chat_row, cursor, from, to, 0);
        setListAdapter(adapter);*/

        loadChatHistory(view.getContext());

        /*FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabSend);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text =  chatTextView.getText().toString();
                chatTextView.setText("");
                new Send(text.toString()).execute();
            }
        });*/
        return view;
    }

    private class Send extends AsyncTask<String, String, JSONObject> {

        ChatDataSource dba=new ChatDataSource(getActivity().getApplicationContext());//Create this object in onCreate() method
        Bundle bundle;


        private String from;

        private String status;
        private String deliveryTime;
        private String chatText;

        public Send(String message_text) {
            from =  prefs.getString("REG_FROM","");
            Log.i(TAG,"from user   = " + from);
            status  = "chat";
            chatText = message_text;
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<>();
            params.add(new BasicNameValuePair("fromu",from));
            params.add(new BasicNameValuePair("to", to_user.replaceAll("[^0-9]", "")));
            params.add(new BasicNameValuePair("status", ""+ status));
            params.add(new BasicNameValuePair("conversation_rowId", conversation_guid));
            params.add(new BasicNameValuePair("text", "" + chatText));

            //MESSAGE SENDING
            JSONObject jObj = json.getJSONFromUrl("http://nodejs-whatnext.rhcloud.com/sendchat", params);

            dba.open();
            WnChatMessage temp= dba.insert(chatText, from, Long.valueOf(conversation_rowId));// Insert record in your DB
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
                    Toast.makeText(getActivity().getApplicationContext(), "The user has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();
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

        private WnMessageRowOptionFragment getVisibleFragment(int index){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if(fragments.isEmpty())
                return null;
            else
            {
                return (WnMessageRowOptionFragment)fragments.get(index + 1);
            }
        }
    }
}
