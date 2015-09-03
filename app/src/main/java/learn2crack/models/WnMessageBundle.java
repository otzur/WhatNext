package learn2crack.models;

import android.os.Bundle;

/**
 * Created by samzaleg on 9/3/2015.
 */
public class WnMessageBundle {
    private String status="";
    private String messageID="";
    private String fromUser="";
    private String type="";
    private String tab="";
    private String selectedOptions="";
    private String conversation_id="";
    private Bundle bundle=null;

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getSelectedOptions() {
        return selectedOptions;
    }

    public String getStatus() {
        return status;
    }

    public String getTab() {
        return tab;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getType() {
        return type;
    }

    public void setSelectedOptions(String selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public WnMessageBundle fillMessageBundleFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        WnMessageBundle result = new WnMessageBundle();
        result.setMessageID(bundle.getString("msg_id",""));
        result.setConversation_id(bundle.getString("c_id", ""));
        result.setFromUser(bundle.getString("fromu",""));
        result.setType(bundle.getString("type",""));
        result.setTab(bundle.getString("tab",""));
        result.setSelectedOptions(bundle.getString("selected_options",""));
        this.bundle = bundle;
        return result;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
