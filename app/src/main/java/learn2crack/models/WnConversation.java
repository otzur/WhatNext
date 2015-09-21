package learn2crack.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by samzaleg on 8/27/2015.
 */
public class WnConversation implements Serializable {
    private long rowId;
    private String conversation_guid;
    private String type;
    private int options_type;
    private int tab;
    private String status;
    private ArrayList<WnContact> contacts;
    private ArrayList<WnMessage> messages;
//    private ArrayList<WnChatMessage> chatMessages;
    private static final long serialVersionUID = -7060210544600464481L;

    public ArrayList<WnContact> getContacts() {
        return contacts;
    }

    public ArrayList<WnMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<WnMessage> messages) {
        this.messages = messages;
    }

    public void addContact(WnContact contact)
    {
        if(contacts == null) {
            contacts = new ArrayList<>();
        }
        contacts.add(contact);
    }

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

    public long getRowId(){return this.rowId;}

    public String getType(){return this.type;}

    public int getOptions_type(){return this.options_type;}

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public void setOptions_type(int options_type) {
        this.options_type = options_type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConversation_guid(String conversation_guid) {
        this.conversation_guid = conversation_guid;
    }

    public String getConversation_guid() {
        return conversation_guid;
    }

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }

    public void addMessage(WnMessage wnMessage) {
        if(messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(wnMessage);
    }

    public void clearMessages() {
        this.messages.clear();
    }
}
