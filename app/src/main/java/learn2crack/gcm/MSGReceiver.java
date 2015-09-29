package learn2crack.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import learn2crack.bl.ObjectManager;
import learn2crack.db.ChatDataSource;
import learn2crack.db.ConversationDataSource;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageStatus;

public class MSGReceiver  extends WakefulBroadcastReceiver {

    private Bundle handleWnChatMessage(Context context, Intent intent) {
        ChatDataSource chatDataSource = new ChatDataSource(context);
        ConversationDataSource dbConversations=new ConversationDataSource(context);
        String fromu,conversation_guid,chatText;
        Bundle extras = intent.getExtras();
        WnConversation conversation;
        fromu = extras.getString("fromu");
        chatText = extras.getString("text");
        conversation_guid = extras.getString("c_id");
        dbConversations.open();
        conversation = dbConversations.getConversationByGUID(conversation_guid);
        dbConversations.close();
        chatDataSource.open();
        chatDataSource.insert(chatText, fromu, conversation.getRowId());
        chatDataSource.close();
        extras.putString("c_id", Long.toString(conversation.getRowId()));
        return extras;
    }




    private Bundle handleWnMessage(Context context, Intent intent){

        String msg_id="", contactPhoneNumber="",type="",tab="",selected_options="",conversation_guid="";
        //WnMessageStatus status;
        String status;
        Bundle extras = intent.getExtras();
        WnConversation conversation = null;

        //status = (WnMessageStatus) extras.getSerializable("status");
        status =  extras.getString("status");
        WnMessageStatus wnMessageStatus = WnMessageStatus.valueOf(status);
        msg_id = extras.getString("msg_id");
        contactPhoneNumber = extras.getString("fromu");
        type = extras.getString("type");
        tab = extras.getString("tab");
        selected_options = extras.getString("selected_options");
        conversation_guid = extras.getString("c_id");

        Log.i("WN MSGReceiver", "status = " + status.toString());
        Log.i("WN MSGReceiver", "conversation_id = " + conversation_guid);


        //WnConversation wnConversation = ObjectManager.createNewConversation(contactPhoneNumber, type, status);
        WnConversation wnConversation = ObjectManager.createNewConversation(contactPhoneNumber, type, WnMessageStatus.valueOf(status));
        wnConversation.setTab(Integer.valueOf(tab));
        wnConversation.setConversation_guid(conversation_guid);

//        WnMessage wnMessage  = ObjectManager.createNewMessage(msg_id,contactPhoneNumber, selected_options, status,0);
        WnMessage wnMessage  = ObjectManager.createNewMessage(msg_id,contactPhoneNumber, selected_options, WnMessageStatus.valueOf(status),0);
        wnConversation.addMessage(wnMessage);


        switch (wnMessageStatus) {
            case NEW:

                // first save conversation into database
                  Long conversationRowId = ObjectManager.saveConversation(wnConversation);
                  wnConversation.setRowId(conversationRowId);
                  extras.putString("c_id", Long.toString(wnConversation.getRowId()));
                  extras.putString("conversation_guid", wnConversation.getConversation_guid());
//                dbConversations.open();
//                conversation = dbConversations.insert(conversation_id,
//                        0, type, tab, contactPhoneNumber, contactName, "New");
//                dbConversations.close();
//                dba.open();
//                dba.insert(msg_id, "message", contactPhoneNumber, selected_options, "New", 0, Long.valueOf(conversation.getRowId()).toString());
//                dba.close();
                break;
            case RESULTS:
            case RESPONSE:

                conversation = ObjectManager.getConversationByGUID(conversation_guid);
                conversation.setStatus(WnMessageStatus.RESULTS);
                WnMessage wnMessage1  = ObjectManager.createNewMessage(msg_id, contactPhoneNumber,selected_options, WnMessageStatus.RESULTS,0);
                conversation.addMessage(wnMessage1);
                ObjectManager.updateConversation(conversation);
                extras.putString("c_id", Long.toString(conversation.getRowId()));
                extras.putString("conversation_guid", conversation.getConversation_guid());

//                dbConversations.update(conversation_guid, 0, type, tab, contactPhoneNumber, contactName,"RESULTS");
//                dbConversations.close();
//                dba.open();
//                dba.insert(msg_id, "message", contactPhoneNumber, selected_options, "RESULTS", 0, Long.valueOf(conversation.getRowId()).toString());
//                dba.close();
                break;
            default:
                Log.e("WN MSGReceiver", "status = " + status.toString() );
                Log.e("WN MSGReceiver", "failed to figure message status");
                return null;
        }

        return extras;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Bundle newExtras = null;
        //WnMessageStatus status = (WnMessageStatus) extras.getSerializable("status");
        String status =  extras.getString("status");
        if(status != null && !status.equals(WnMessageStatus.CHAT.name())) {
            newExtras= handleWnMessage(context,intent);
        }
        if(status != null && status.equals(WnMessageStatus.CHAT.name())){
            newExtras= handleWnChatMessage(context,intent);
        }


        // change status to enum
        newExtras.remove("status");
        WnMessageStatus wnMessageStatus = WnMessageStatus.valueOf(status);
        newExtras.putSerializable("status",wnMessageStatus);
        intent.removeExtra("INFO");
        intent.putExtra("INFO", newExtras);//newExtras);

        ComponentName comp = new ComponentName(context.getPackageName(),MSGService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}