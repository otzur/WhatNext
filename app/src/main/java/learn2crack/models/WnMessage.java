package learn2crack.models;

import android.graphics.Bitmap;

/**
 * Created by otzur on 7/12/2015.
 */
public class WnMessage {

    private long id;
    private String message_id;
    private String message;
    private String user;
    private String user_name;
    //private String to;
    //private String to_name;
    private String option_selected;
    private String type;
    private String Status;
    private String delivery_date;
    private int filled_by_you;
    private long conversation_id;
    private Bitmap user_photo;
    private int tab;

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // user phone number
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // user name
    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name)
    {
        this.user_name = user_name;
    }
    // user photto
    public Bitmap getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(Bitmap user_photo) {
        this.user_photo = user_photo;
    }

    public String getOption_selected() {
        return option_selected;
    }

    public void setOption_selected(String option_selected) {
        this.option_selected = option_selected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDelivery_date() {
        return delivery_date;
        //return Utils.DateFormat(delivery_date);
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }


    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setFilled_by_you(int filled_by_you){this.filled_by_you = filled_by_you;}

    public int getFilled_by_you(){ return this.filled_by_you;}

    public void setConversation_id(long conversation_id) {this.conversation_id = conversation_id;}

    public long getConversation_id(){return this.conversation_id;}

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return message;
    }

    public String getMessage_id() {
        return message_id;
    }


}
