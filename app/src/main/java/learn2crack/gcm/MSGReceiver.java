package learn2crack.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import learn2crack.db.ConversationDataSource;
import learn2crack.db.MessageDataSource;
import learn2crack.models.WnConversation;

public class MSGReceiver  extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Bundle newExtras = new Bundle();
        MessageDataSource dba=new MessageDataSource(context);
        ConversationDataSource dbConversations=new ConversationDataSource(context);
        String msg_id,fromu,type,tab,status,selected_options,conversation_id;
        msg_id = extras.getString("msg_id");
        fromu = extras.getString("fromu");
        type = extras.getString("type");
        tab = extras.getString("tab");
        status = extras.getString("status");
        selected_options = extras.getString("selected_options");
        conversation_id = extras.getString("c_id");//the server send the unique converation id !!!
        newExtras.putString("msg_id", msg_id);
        newExtras.putString("from", fromu);
        newExtras.putString("type", type);
        newExtras.putString("tab", tab);
        newExtras.putString("status", status);
        newExtras.putString("selected_options", selected_options);
        newExtras.putString("conversation_id", conversation_id);
        String to = context.getSharedPreferences("chat", 0).getString("REG_FROM","");
        WnConversation conversation;
        switch (status) {
            case "new":
                dbConversations.open();
                conversation = dbConversations.insert(conversation_id,2,
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
                return;
        }
        newExtras.putString("c_id", Long.toString(conversation.getId()));
        intent.putExtra("INFO",newExtras);
        ComponentName comp = new ComponentName(context.getPackageName(),MSGService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}