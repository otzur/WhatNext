package learn2crack.models;

import android.graphics.Bitmap;

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
    private Bitmap user_photo;
    private String user_name;
    private String contact_phone_number;
    private String status;

    public String getUpdate_datetime() {
        return update_datetime;
    }

    public void setUpdate_datetime(String update_datetime) {
        this.update_datetime = update_datetime;
    }

    private String update_datetime;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    // user name
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    // user photo
    public Bitmap getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(Bitmap user_photo) {
        this.user_photo = user_photo;
    }

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

    public String getContact_phone_number() {
        return contact_phone_number;
    }

    public void setContact_phone_number(String contact_phone_number) {
        this.contact_phone_number = contact_phone_number;
    }


}
