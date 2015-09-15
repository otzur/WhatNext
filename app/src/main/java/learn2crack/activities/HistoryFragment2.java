package learn2crack.activities;

/**
 * Created by otzur on 9/8/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import learn2crack.adapters.HistoryRVAdapter;
import learn2crack.chat.R;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnMessage;

public class HistoryFragment2 extends Fragment {

    //private List<Person> persons;
    private List<WnMessage> messages;
    private RecyclerView rv;

    public HistoryFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("WN", "inside on create history fragment");
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_person, container, false);
        rv = (RecyclerView) inflater.inflate( R.layout.message_recyclerview, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
//        LinearLayoutManager llm = new LinearLayoutManager(rv.getContext());
//        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);


        initializeData();
        initializeAdapter();

        return rv;
    }

    private void initializeData(){

        messages = new ArrayList<>();
        MessageDataSource data_source = new MessageDataSource(rv.getContext());
        data_source.open();
        messages = data_source.getAllMessages();
        data_source.close();

//        persons = new ArrayList<>();
//        persons.add(new Person("Emma Wilson", "23 years old", R.drawable.emma));
//        persons.add(new Person("Lavery Maiss", "25 years old", R.drawable.lavery));
//        persons.add(new Person("Lillie Watts", "35 years old", R.drawable.lillie));
//        persons.add(new Person("Emma Wilson", "23 years old", R.drawable.emma));
//        persons.add(new Person("Lavery Maiss", "25 years old", R.drawable.lavery));
//        persons.add(new Person("Lillie Watts", "35 years old", R.drawable.lillie));
//        persons.add(new Person("Emma Wilson", "23 years old", R.drawable.emma));
//        persons.add(new Person("Lavery Maiss", "25 years old", R.drawable.lavery));
//        persons.add(new Person("Lillie Watts", "35 years old", R.drawable.lillie));
//        persons.add(new Person("Emma Wilson", "23 years old", R.drawable.emma));
//        persons.add(new Person("Lavery Maiss", "25 years old", R.drawable.lavery));
//        persons.add(new Person("Lillie Watts", "35 years old", R.drawable.lillie));
    }

    private void initializeAdapter() {

        HistoryRVAdapter adapter = new HistoryRVAdapter(messages);
        rv.setAdapter(adapter);
    }
}
