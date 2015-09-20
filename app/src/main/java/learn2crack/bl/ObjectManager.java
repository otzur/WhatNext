package learn2crack.bl;

import android.util.Log;

import learn2crack.activities.MainActivity;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnContact;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;

/**
 * Created by otzur on 9/17/2015.
 */
public class ObjectManager {

    static final String TAG = "WN";

    public static WnConversation createNewConversation(String name, String phoneNumber, String type, String status) {

        WnConversation wnConversation = new WnConversation();
        wnConversation.setId(-1);
        WnContact contact = new WnContact(name, phoneNumber);
        wnConversation.addContact(contact);
        wnConversation.setType(type);
        wnConversation.setStatus(status);
        //wnConversation.setN_users(2);

        return wnConversation;
    }


    public static long saveConversation(WnConversation wnConversation) {

        ConversationDataSource dbConversations = new ConversationDataSource(MainActivity.getAppContext());
        dbConversations.open();
        long wnConversationId= dbConversations.insert(wnConversation.getConversation_id(), 0 ,
                wnConversation.getType(), Integer.valueOf(wnConversation.getTab()).toString(),
                wnConversation.getContacts().get(0).getPhoneNumber(),wnConversation.getContacts().get(0).getName(),
                wnConversation.getStatus()).getId();

        dbConversations.close();

        for (WnMessage message : wnConversation.getMessages())
        {
            saveMessage(message, wnConversationId);
        }


        Log.i(TAG, "saved conversation in databased");

        return wnConversationId ;
    }

    public static WnMessage createNewMessage(String message_id, String message , String userPhone,
                                             String option_selected, String Status , String delivery_date,
                                             int filled_by_you, String conversation_id) {

        WnMessage wnMessage = new WnMessage();
        wnMessage.setId(-1);
        wnMessage.setMessage_id(message_id);
        wnMessage.setMessage(message);
        wnMessage.setUser(userPhone);
        //wnMessage.setUserName(contactName);
        wnMessage.setOption_selected(option_selected);
        //wnMessage.setType(type);
        wnMessage.setStatus(Status);
        wnMessage.setDelivery_date(delivery_date);
        wnMessage.setFilled_by_you(filled_by_you);
        wnMessage.setConversation_id(conversation_id);
        //wnMessage.setTab(tab);

        return wnMessage;

    }



    private static void saveMessage(WnMessage message, long wnConversationId) {

        MessageDataSource dba=new MessageDataSource(MainActivity.getAppContext());//Create this object in onCreate() method
        if(message.getId() == -1)
        {
            dba.open();
            dba.insert(message.getMessage_id(), message.getMessage(), message.getUser(),
                    message.getOption_selected(), message.getStatus(), message.getFilled_by_you(),
                    Long.valueOf(wnConversationId).toString());// Insert record in your DB
            dba.close();

            Log.i(TAG, "saved message in databased");
        }

    }
}
