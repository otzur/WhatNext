package learn2crack.activities;

/**
 * Created by ${USER} on ${DATE}.
 */

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private RecyclerView.Adapter adapter;

    public HistoryFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume (){
        super.onResume();
        ((HistoryRVAdapter) adapter).setOnItemClickListener(new HistoryRVAdapter.HistoryItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i("WN", "click  item on position " + position);
                Snackbar.make(v, "click  item on position " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Snackbar.make(v, "Delivery_date = " + messages.get(position).getDelivery_date(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
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

    }

    private void initializeAdapter() {

        adapter = new HistoryRVAdapter(messages);
        rv.setAdapter(adapter);
    }
}
