package learn2crack.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import learn2crack.db.ChatDataSource;
import learn2crack.db.ConversationDataSource;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessageBundle;

public class MSGReceiver  extends WakefulBroadcastReceiver {

    private Bundle handleWnChatMessage(Context context, Intent intent) {
        ChatDataSource chatDataSource = new ChatDataSource(context);
        ConversationDataSource dbConversations=new ConversationDataSource(context);
        String fromu="",conversation_id="",chatText="";
        Bundle extras = intent.getExtras();
        WnConversation conversation;
        fromu = extras.getString("fromu");
        chatText = extras.getString("text");
        conversation_id = extras.getString("c_id");
        dbConversations.open();
        conversation = dbConversations.getConversationByUniqeID(conversation_id);
        dbConversations.close();
        chatDataSource.open();
        chatDataSource.insert(chatText, fromu, conversation.getId());
        chatDataSource.close();
        extras.putString("c_id", Long.toString(conversation.getId()));
        return extras;
    }


    private Bundle handleWnMessage(Context context, Intent intent){

        MessageDataSource dba=new MessageDataSource(context);
        ConversationDataSource dbConversations=new ConversationDataSource(context);
        String msg_id="",fromu="",type="",tab="",status="",selected_options="",conversation_id="";
        Bundle extras = intent.getExtras();
        WnConversation conversation;
        String to = context.getSharedPreferences("chat", 0).getString("REG_FROM","");
        status = extras.getString("status");
        msg_id = extras.getString("msg_id");
        fromu = extras.getString("fromu");
        type = extras.getString("type");
        tab = extras.getString("tab");
        selected_options = extras.getString("selected_options");
        conversation_id = extras.getString("c_id");
        switch (status) {
            case "new":
                dbConversations.open();
                conversation = dbConversations.insert(conversation_id, 2,
                        Integer.valueOf(tab) + 1, type, tab);
                dbConversations.close();
                dba.open();
                dba.insert(msg_id, "message", fromu, to, selected_options, type, "received", 0, conversation.getId());
                dba.close();
                break;
            case "response":
                dbConversations.open();
                conversation = dbConversations.getConversationByUniqeID(conversation_id);
                dbConversations.close();
                dba.open();
                dba.insert(msg_id, "message", fromu, to, selected_options, type, status, 0, conversation.getId());
                dba.close();
                break;
            default:
                Log.e("WN", "faild to figure message status");
                return null;
        }
        extras.putString("c_id", Long.toString(conversation.getId()));
        extras.putString("conversation_id", conversation.getConversation_id());
        return extras;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Bundle newExtras = null;
        String status = extras.getString("status");
        if(status != null && !status.equals("chat")) {
            newExtras= handleWnMessage(context,intent);
        }
        if(status != null && status.equals("chat")){
            newExtras= handleWnChatMessage(context,intent);
        }
        intent.removeExtra("INFO");
        intent.putExtra("INFO", newExtras);//newExtras);
        ComponentName comp = new ComponentName(context.getPackageName(),MSGService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}