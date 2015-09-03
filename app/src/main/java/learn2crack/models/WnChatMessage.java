package learn2crack.models;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class WnChatMessage {
    private long id;
    private long conversation_id;
    private String delivery_date;
    private String chat_text;
    private String from;

    public long getConversation_id() {
        return conversation_id;
    }

    public long getId() {
        return id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() { return from; }

    public String getChat_text() {
        return chat_text;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChat_text(String chat_text) {
        this.chat_text = chat_text;
    }

    public void setConversation_id(long conversation_id) {
        this.conversation_id = conversation_id;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }
}

