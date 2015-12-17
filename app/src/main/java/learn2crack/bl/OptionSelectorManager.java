package learn2crack.bl;

import java.util.ArrayList;
import java.util.List;

import learn2crack.models.WnMessageRowOption;

/**
 * Created by otzur on 10/8/2015.
 */
public class OptionSelectorManager {

    public List<WnMessageRowOption> getList() {
        return list;
    }

    public void setList(List<WnMessageRowOption> list) {
        this.list = list;
    }

    private List<WnMessageRowOption> list;

    public OptionSelectorManager(int numberOfOptions) {
        if(numberOfOptions > 0) { //him and her
            createOptions(numberOfOptions);
        }
        else{
            createEmptyOptions();
        }
    }

    public static int getNumberOfOptionsByTab(int tab){

        int numberOfOptions = 0;
        switch (tab){
            case 0:
                numberOfOptions = 2;
                break;
            case 1:
                numberOfOptions=  5;
                break;
            case 2:
                numberOfOptions = 8;
                break;
        }
        return numberOfOptions;
    }

    private List<WnMessageRowOption> createOptions(int numberOfOptions) {
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
        return list;
    }

    private List<WnMessageRowOption> createEmptyOptions() {
        list = new ArrayList<>();
        //list.add(get("Press to add option"));
        return list;
    }

    private WnMessageRowOption get(String s) {
        return new WnMessageRowOption(s);

    }


    public WnMessageRowOption getWnMessageRowById(int id){
        return list.get(id);
    }
}
