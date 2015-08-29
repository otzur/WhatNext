package learn2crack.models;

import android.support.v4.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samzaleg on 8/24/2015.
 */
public class WnMessageResult {
    List<String> checked_by_you;
    List<String> matched;
    String type;
    boolean allUsersResponded;

    public boolean isAllUsersResponded() {
        return allUsersResponded;
    }

    public void setAllUsersResponded(boolean allUsersResponded) {
        this.allUsersResponded = allUsersResponded;
    }

    public void setChecked_by_you(List<String> checked_by_you){this.checked_by_you = checked_by_you;}

    public List<String> getChecked_by_you(){return this.checked_by_you;}

    public void addChecked_by_you(String checked){
        if(this.checked_by_you == null){
            this.checked_by_you = new ArrayList<String>();
        }
        this.checked_by_you.add(checked);
    }

    public void setMatched(List<String> matched){ this.matched = matched;}

    public List<String> getMatched(){return this.matched;}

    public void addMatched(String matched){
        if(this.matched == null){
            this.matched = new ArrayList<String>();
        }
        this.matched.add(matched);
    }

    public void setType(String type){this.type = type;}

    public String getType(){return this.type;}
}
