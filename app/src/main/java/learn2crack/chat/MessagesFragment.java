package learn2crack.chat;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import java.util.List;


public class MessagesFragment extends ListFragment {


    private MessageDataSource datasource;
    private MessageDatabaseHelper DBHelper;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);

        datasource = new MessageDataSource(view.getContext());
        datasource.open();

        List<WnMessage> values = datasource.getAllMessages();

        Cursor cursor = datasource.getAllData();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView

       // ArrayAdapter<WnMessage> adapter = new ArrayAdapter<>(view.getContext(),android.R.layout.simple_list_item_1, values);
       // setListAdapter(adapter);

        String[] from = new String[] { DBHelper.KEY_ROWID,  DBHelper.KEY_FROM, DBHelper.KEY_TO , DBHelper.KEY_OPTION_SELECTED, DBHelper.KEY_CREATION_DATE };
        int[] to = new int[] {R.id.message_id,  R.id.from_user, R.id.to_user ,  R.id.selected_options,  R.id.creation_date };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(view.getContext(), R.layout.message_row, cursor, from, to, 0);
        setListAdapter(adapter);
        //setListAdapter(new SimpleCursorAdapter(this, R.layout.note_item, cursor, NotesListActivity.FROM, NotesListActivity.TO));

        return view;

    }
}
