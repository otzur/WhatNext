package learn2crack.activities;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import learn2crack.bl.OptionSelectorManager;
import learn2crack.models.WnMessageRowOption;
import learn2crack.adapters.WnMessageRowOptionArrayAdapter;


/**
 * Created by otzur on 6/3/2015.
 */
public class WnMessageRowOptionFragment extends ListFragment {

    private ArrayAdapter<WnMessageRowOption> adapter;
    private List<WnMessageRowOption> list;
    private int maxSelectedNumber = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle=getArguments();
        //here is your list array
        int numberOfOptions = bundle.getInt("numberOfOptions");

        switch (numberOfOptions){
            case 2:
                maxSelectedNumber = 1;
                break;
            case 5:
                maxSelectedNumber = 2;
                break;
            case 8:
            default:
                maxSelectedNumber = 3;
        }

        // create an array of Strings, that will be put to our ListActivity
        OptionSelectorManager optionSelectorManager = new OptionSelectorManager(numberOfOptions);
        list  = optionSelectorManager.getList();
        adapter = new WnMessageRowOptionArrayAdapter((android.app.Activity) inflater.getContext(), list, maxSelectedNumber);

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
}
