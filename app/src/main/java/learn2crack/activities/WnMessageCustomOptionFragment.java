package learn2crack.activities;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import learn2crack.adapters.WnMessageCustomOptionArrayAdapter;
import learn2crack.adapters.WnMessageRowOptionArrayAdapter;
import learn2crack.bl.OptionSelectorManager;
import learn2crack.models.WnMessageRowOption;


/**
 * Created by otzur on 6/3/2015.
 */
public class WnMessageCustomOptionFragment extends ListFragment {

    private WnMessageCustomOptionArrayAdapter adapter;
    private List<WnMessageRowOption> list;
    private int maxSelectedNumber = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle=getArguments();
        //here is your list array
        int numberOfOptions = bundle.getInt("numberOfOptions");
        maxSelectedNumber = 1;
        // create an array of Strings, that will be put to our ListActivity
        OptionSelectorManager optionSelectorManager = new OptionSelectorManager(-1); // -1 for custom options
        list  = optionSelectorManager.getList();
        adapter = new WnMessageCustomOptionArrayAdapter((android.app.Activity) inflater.getContext(), list, 1);

        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public String getSelectedOptions() {


        ArrayList<String>  selectedItems   = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isSelected())
                selectedItems.add(String.valueOf(i) + " ");
        }
        return selectedItems.toString();
    }

    public void addOption(String option){
        adapter.addOption(new WnMessageRowOption(option));
        adapter.notifyDataSetChanged();
    }
}
