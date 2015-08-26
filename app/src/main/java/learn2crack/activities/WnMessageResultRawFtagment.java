package learn2crack.activities;

import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import learn2crack.adapters.WnMessageResultRawArrayAdapter;
import learn2crack.adapters.WnMessageRowOptionArrayAdapter;
import learn2crack.chat.R;
import learn2crack.models.WnMessageResultRaw;
import learn2crack.models.WnMessageRowOption;

public class WnMessageResultRawFtagment extends ListFragment {

    private ArrayAdapter<WnMessageResultRaw> adapter;
    private List<WnMessageResultRaw> list;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle=getArguments();
        //here is your list array
        int numberOfOptions = bundle.getInt("numberOfOptions");

        // create an array of Strings, that will be put to our ListActivity
        adapter = new WnMessageResultRawArrayAdapter((android.app.Activity) inflater.getContext(), getMatched(numberOfOptions));
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private List<WnMessageResultRaw> getMatched(int numberOfOptions ) {


        list = new ArrayList<>();

        switch (numberOfOptions)
        {
            case 2:
            {
                list.add(get("Not Interesting"));
                list.add(get("Interesting"));

            }break;
            case 5:
            {
                list.add(get("Not Interesting"));
                list.add(get("Must have another date"));
                list.add(get("Friends with benefits"));
                list.add(get("Start as friends"));
                list.add(get("Interesting - Stop seeing others"));

            }break;
            case 8:
            {
                list.add(get("Not going to happen"));
                list.add(get("Friends With Benefits"));
                list.add(get("Another date"));
                list.add(get("One Night stand"));
                list.add(get("Meet the parents"));
                list.add(get("Undefined"));
                list.add(get("Friends  only"));
                list.add(get("In other time maybe"));

            }break;
        }

        //list.add(get("Not Interesting"));
        //list.add(get("Interesting"));
        // Initially select one of the items
        //list.get(1).setSelected(true);
        return list;
    }


    private WnMessageResultRaw get(String s) {
        return new WnMessageResultRaw(s);
    }

    /*public String getSelectedOptions() {


        ArrayList<String>  selectedItems   = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isSelected())
                selectedItems.add(String.valueOf(i) + " ");
        }
        return selectedItems.toString();
    }*/
}