package learn2crack.bl;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.UUID;

import learn2crack.activities.MainActivity;
import learn2crack.db.ChatDataSource;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnContact;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageStatus;
import learn2crack.utilities.Contacts;

/**
 * Created by otzur on 9/17/2015.
 */
public class ObjectManager {

    static final String TAG = "WN";

    public static WnConversation getConversationByGUID(Context context, String conversation_guid) {

        ConversationDataSource dbConversations = new ConversationDataSource(context);
        dbConversations.open();
        WnConversation conversation = dbConversations.getConversationByGUID(conversation_guid);

        String user_phone = conversation.getContacts().get(0).getPhoneNumber();
        conversation.getContacts().get(0).setName(Contacts.getContactName(context, user_phone));
        return conversation;
    }

    public static WnConversation createNewConversation(Context context, String contactPhoneNumber, String type) {


        UUID uuid = UUID.randomUUID();
        String contactName  = (Contacts.getContactName(context, contactPhoneNumber));
        WnConversation wnConversation = new WnConversation();
        wnConversation.setRowId(-1);

        Bitmap contactPhoto = Contacts.retrieveContactPhoto(MainActivity.getAppContext(), contactPhoneNumber);
        WnContact contact = new WnContact(contactName, contactPhoneNumber, contactPhoto);
        //contact.setPhoto(Contacts.retrieveContactPhoto(MainActivity.getAppContext(), contactPhoneNumber));
        wnConversation.addContact(contact);
        wnConversation.setType(type);
        wnConversation.setStatus(WnMessageStatus.NEW);
        wnConversation.setConversation_guid(uuid.toString());

        return wnConversation;
    }


    public static void updateConversation(Context context , WnConversation wnConversation)
    {
        ConversationDataSource dbConversations = new ConversationDataSource(context);
        dbConversations.open();
        long wnConversationId = dbConversations.update(wnConversation.getConversation_guid(), wnConversation.getOptions_type(),
                wnConversation.getType(), Integer.valueOf(wnConversation.getTab()).toString(), wnConversation.getContacts().get(0).getPhoneNumber(),
                wnConversation.getContacts().get(0).getName(), wnConversation.getStatus());

        dbConversations.close();

        Log.i(TAG, "Update conversation completed rowid = " + wnConversation.getRowId());
        for (WnMessage message : wnConversation.getMessages())
        {
            if(message.getRowId() == -1)
                saveMessage(message, wnConversation.getRowId());
        }

    }

    public static Cursor getConversationChatMessages(Context context, WnConversation wnConversation){
        ChatDataSource datasource = new ChatDataSource(context);
        ConversationDataSource conversationDataSource = new ConversationDataSource(context);
        conversationDataSource.open();
        return datasource.getAllDataByConversationRowID(Long.toString(wnConversation.getRowId()));
    }


    public static long saveConversation(Context context ,WnConversation wnConversation) {

        ConversationDataSource dbConversations = new ConversationDataSource(context);
        dbConversations.open();
        long wnConversationId= dbConversations.insert(wnConversation.getConversation_guid(), 0 ,
                wnConversation.getType(), Integer.valueOf(wnConversation.getTab()).toString(),
                wnConversation.getContacts().get(0).getPhoneNumber(),wnConversation.getContacts().get(0).getName(),
                wnConversation.getStatus()).getRowId();

        dbConversations.close();

        for (WnMessage message : wnConversation.getMessages())
        {
            if(message.getRowId() == -1)
                saveMessage(message, wnConversationId);
        }


        Log.i(TAG, "saved conversation in databased");

        return wnConversationId ;
    }

    public static WnMessage createNewMessage(String msg_id, String contactPhoneNumber, String selected_options, WnMessageStatus status, int filled_by_you) {

        WnMessage wnMessage = new WnMessage();

        wnMessage.setRowId(-1);
        wnMessage.setMessage_guid(msg_id);
        wnMessage.setMessage("message");
        wnMessage.setUser(contactPhoneNumber);
        //wnMessage.setUserName(contactName);
        wnMessage.setOption_selected(selected_options);
        //wnMessage.setType(type);
        wnMessage.setStatus(status);
        //wnMessage.setDelivery_date(delivery_date);
        wnMessage.setFilled_by_you(filled_by_you);
        //wnMessage.setConversation_guid(conversation_id);
        //wnMessage.setTab(tab);

        return wnMessage;

    }

    public static WnMessage createNewMessage( String userPhone,
                                             String option_selected, WnMessageStatus status ,int filled_by_you) {

        UUID messageGuid = UUID.randomUUID();
        return createNewMessage(messageGuid.toString(),userPhone, option_selected, status, filled_by_you);
    }



    private static void saveMessage(WnMessage message, long wnConversationId) {

        MessageDataSource dba=new MessageDataSource(MainActivity.getAppContext());//Create this object in onCreate() method
        if(message.getRowId() == -1)
        {
            dba.open();
            dba.insert(message.getMessage_guid(), message.getMessage(), message.getUser(),
                    message.getOption_selected(), message.getStatus(), message.getFilled_by_you(),
                    Long.valueOf(wnConversationId).toString());// Insert record in your DB
            dba.close();

            Log.i(TAG, "saved message in databased");
        }

    }


}
