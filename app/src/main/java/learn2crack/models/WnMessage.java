package learn2crack.models;

/**
 * Created by otzur on 7/12/2015.
 */
public class WnMessage {

    private long id;
    private String message_id;
    private String message;
    private String user;
    private String to;
    private String option_selected;
    private String type;
    private String Status;
    private String delivery_date;
    private int filled_by_you;
    private String associated_to_message_id;


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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public void setAssociated_to_message_id(String associated_to_message_id){ this.associated_to_message_id = associated_to_message_id;}

    public String getAssociated_to_message_id(){return this.associated_to_message_id;}

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return message;
    }

    public String getMessage_id() {
        return message_id;
    }
}
