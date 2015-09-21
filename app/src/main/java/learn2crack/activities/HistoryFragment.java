package learn2crack.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import learn2crack.chat.R;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.DatabaseHelper;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageResult;

public class HistoryFragment extends ListFragment {


    private DatabaseHelper DBHelper;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String msg_id =(String) ((TextView) v.findViewById(R.id.message_id)).getText();
        String filled_by_you =(String) ((TextView) v.findViewById(R.id.filled_by_you)).getText();
        if(filled_by_you.equals("0")){
            String status =(String) ((TextView) v.findViewById(R.id.status)).getText();
            if(status.equals("received")){
                MessageDataSource data_source = new MessageDataSource(getActivity().getApplicationContext());
                data_source.open();
                WnMessageResult wnResult= data_source.getResultsForMessageID(msg_id);
                if(!wnResult.isAllUsersResponded()){
                    WnMessage message = data_source.getMessage(msg_id);
                    data_source.close();
                    ConversationDataSource c_DataSource = new ConversationDataSource(getActivity().getApplicationContext());
                    c_DataSource.open();
                    WnConversation conversation = c_DataSource.getConversationByRowID(message.getConversation_rowId());
                    c_DataSource.close();
                    Bundle args = new Bundle();
                    args.putString("mobno", message.getUser());
                    //args.putString("user_name", message.getUserName());
                    args.putString("type", conversation.getType());
                    args.putString("tab", Integer.toString(conversation.getTab()));
                    args.putString("c_id", Long.toString(conversation.getRowId()));
                    args.putString("conversation_id", conversation.getConversation_guid());
                    args.putString("status", "received");
                    Intent chat = new Intent(getActivity(), WnMessageReceiveActivity.class);
                    chat.putExtra("INFO", args);
                    chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(chat);
                    return;
                }
                data_source.close();
            }
        }
        Bundle args = new Bundle();
        args.putString("msg_id", msg_id);
        String c_id =(String) ((TextView) v.findViewById(R.id.c_id)).getText();
        args.putString("c_id", c_id);
        //Intent chat =chat = new Intent(getActivity(), WnMessageResultActivity.class);
        Intent chat =chat = new Intent(getActivity(), ResultActivity.class);
        chat.putExtra("INFO", args);
        chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().getApplicationContext().startActivity(chat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);

        MessageDataSource datasource = new MessageDataSource(view.getContext());
        datasource.open();

        //List<WnMessage> values = datasource.getAllMessages();

        Cursor cursor = datasource.getAllData();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView

       // ArrayAdapter<WnMessage> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1, values);
       // setListAdapter(adapter);

        String[] from = new String[] { DBHelper.KEY_ROWID, DBHelper.KEY_MESSAGE_GUID,  DBHelper.KEY_USER,
                DBHelper.KEY_OPTION_SELECTED,DBHelper.KEY_STATUS, DBHelper.KEY_CREATION_DATE ,DBHelper.KEY_CONVERSATION_ROW_ID, DBHelper.KEY_FILLED_BY_YOU};
        int[] to = new int[] {R.id.row_id, R.id.message_id,  R.id.from_user,  R.id.selected_options, R.id.status, R.id.creation_date ,R.id.c_id, R.id.filled_by_you};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(view.getContext(), R.layout.history_row, cursor, from, to, 0);
        setListAdapter(adapter);
        //setListAdapter(new SimpleCursorAdapter(this, R.layout.note_item, cursor, NotesListActivity.FROM, NotesListActivity.TO));

        return view;

    }
}
