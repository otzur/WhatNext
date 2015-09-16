package learn2crack.models;

import android.util.Log;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class WnConversation {
    private long id;
    private String conversation_id;
    private String type;
    private int n_users;
    private int options_type;
    private int tab;

    public long getId(){return this.id;}

    public String getType(){return this.type;}

    public int getN_users(){return this.n_users;}

    public int getOptions_type(){return this.options_type;}

    public void setId(long id) {
        this.id = id;
    }

    public void setN_users(int n_users) {
        this.n_users = n_users;
    }

    public void setOptions_type(int options_type) {
        this.options_type = options_type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }
}