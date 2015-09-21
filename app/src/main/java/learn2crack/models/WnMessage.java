package learn2crack.models;

import java.io.Serializable;

/**
 * Created by otzur on 7/12/2015.
 */
public class WnMessage implements Serializable {

    private long rowId;
    private String message_guid;
    private String message;
    private String user;
    private String option_selected;
    private String Status;
    private String delivery_date;
    private int filled_by_you;
    private String conversation_rowId;
    //private Bitmap user_photo;
    private static final long serialVersionUID = -7060210544600464481L;


    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
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

//    // user photto
//    public Bitmap getUser_photo() {
//        return user_photo;
//    }
//
//    public void setUser_photo(Bitmap user_photo) {
//        this.user_photo = user_photo;
//    }

    public String getOption_selected() {
        return option_selected;
    }

    public void setOption_selected(String option_selected) {
        this.option_selected = option_selected;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    public String getDelivery_date() {
        return delivery_date;
        //return Utils.DateFormat(delivery_date);
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }


    public void setMessage_guid(String message_guid) {
        this.message_guid = message_guid;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setFilled_by_you(int filled_by_you){this.filled_by_you = filled_by_you;}

    public int getFilled_by_you(){ return this.filled_by_you;}

    public void setConversation_rowId(String conversation_rowId) {this.conversation_rowId = conversation_rowId;}

    public String getConversation_rowId(){return this.conversation_rowId;}

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return message;
    }

    public String getMessage_guid() {
        return message_guid;
    }


}
