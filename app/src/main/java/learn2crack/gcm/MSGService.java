package learn2crack.gcm;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import learn2crack.activities.ResultActivity;
import learn2crack.activities.WnMessageChatActivity;
import learn2crack.activities.WnMessageDetailActivity;
import learn2crack.activities.WnMessageReceiveActivity;
import learn2crack.bl.ObjectManager;
import learn2crack.chat.R;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMessage;
import learn2crack.models.WnMessageStatus;


public class MSGService extends IntentService {

    SharedPreferences prefs;
    NotificationCompat.Builder notification;
    NotificationManager manager;
    WnConversation wnConversation= null;


    public MSGService() {
        super("MSGService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        //WnMessageStatus status = (WnMessageStatus) extras.getSerializable("status");
        WnMessageStatus status = WnMessageStatus.valueOf(extras.getString("status"));
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        prefs = getSharedPreferences("Chat", 0);


        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e("L2C","Error");

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e("L2C", "Error");

            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                if(!prefs.getString("CURRENT_ACTIVE","").equals(extras.getString("fromu"))) {
                    extras = extras.getBundle("INFO");
                    wnConversation = ObjectManager.getConversationByGUID(getApplicationContext(), extras.getString("c_id"));
                    if (wnConversation != null) {
                        if (!status.equals(WnMessageStatus.CHAT)) {
                            WnMessage wnMessage = ObjectManager.createNewMessage(extras.getString("msg_id"), extras.getString("fromu"),
                                    extras.getString("selected_options"), status, 0);
                            //extras.getString("selected_options"), WnMessageStatus.valueOf(extras.getString("status")),0);
                            wnConversation.addMessage(wnMessage);

                            sendNotification(wnConversation);
                        } else {
                            sendChatNotification(wnConversation);
                        }
                    }
                    Log.i("WN", "Received: " + extras.getString("msg_id"));
                    Log.i("WN", "MSGService type: " + extras.getString("type"));
                    MSGReceiver.completeWakefulIntent(intent);
                }
            }
        }

    }

    private void sendNotification(WnConversation wnConversation) {
        Bundle args = new Bundle();
        Intent chat = new Intent(this, WnMessageReceiveActivity.class);
        switch (wnConversation.getStatus()) {
            case NEW:
                wnConversation.setStatus(WnMessageStatus.RECEIVED);
                wnConversation.getMessages().get(0).setStatus(WnMessageStatus.RECEIVED);
                wnConversation.getMessages().get(0).setRowId(1);
                args.putSerializable("conversation", wnConversation);
                break;
            case RECEIVED:
            case RESULTS:
            case RESPONSE:
                args.putSerializable("conversation", wnConversation);
                break;
            default:
                Log.e("WN MSGService","failed to figure message status");
                return;
        }

        chat.putExtra("INFO", args);

        Intent intent = new Intent("wn_message_receiver");
        intent.putExtra("INFO", args);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        notification = new NotificationCompat.Builder(this);

        String name =  wnConversation.getContacts().get(0).getName();
        notification.setContentTitle("New WN message from " + name);
        notification.setTicker("New WN Message!");
        notification.setSmallIcon(R.drawable.ic_discuss);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                chat, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }

    //private void sendChatNotification(String from, String c_id ,int tab){
    private void sendChatNotification(WnConversation conversation){
        Bundle args = new Bundle();
        args.putSerializable("conversation", conversation);
        //args.putString("c_id", c_id);
        //args.putInt("numberOfOptions", tab);
        //update current chat if active
        Intent intent = new Intent("wn_chat_receiver");
        intent.putExtra("INFO", args);
        String name =  conversation.getContacts().get(0).getName();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("chat from " + name);
        notification.setTicker("New WN Chat Message!");
        notification.setSmallIcon(R.drawable.ic_discuss);
        Intent chat = new Intent(this, WnMessageChatActivity.class);
        chat.putExtra("INFO", args);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                chat, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }


//    private void sendNotification(String msg_id, String from, String mobno,String tab,String selected_options,
//                                  String status , String type, String c_id, String conversation_id) {
//
//        Bundle args = new Bundle();
//        //MessageDataSource dba=new MessageDataSource(getApplicationContext());
//        //ConversationDataSource dbConversations=new ConversationDataSource(getApplicationContext());
//        String to = getSharedPreferences("chat",0).getString("REG_FROM","");
//        Intent chat = null;
//        switch (status) {
//            case "New":
//                args.putString("msg_id", msg_id);
//                args.putString("mobno", mobno);
//                args.putString("type", type);
//                args.putString("tab", tab);
//                args.putString("selected_options", selected_options);
//                args.putString("c_id", c_id);
//                args.putString("conversation_id", conversation_id);
//                args.putString("status", "Received");
//                chat = new Intent(this, WnMessageReceiveActivity.class);
//                break;
//            case "Response":
//                args.putString("c_id", c_id);
//                args.putString("status", "Response");
//                chat = new Intent(this, ResultActivity.class);
//                break;
//            case "chat":
//                args.putString("c_id", c_id);
//                args.putString("status", "chat");
//                chat = new Intent(this, ResultActivity.class);
//                break;
//            default:
//                Log.e("WN MSGService","failed to figure message status");
//                return;
//        }
//
//        chat.putExtra("INFO", args);
//
//        Intent intent = new Intent("wn_message_receiver");
//        intent.putExtra("INFO", args);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//
//        notification = new NotificationCompat.Builder(this);
//
//        notification.setContentTitle("New WN message from " + from);
//
//
//        notification.setTicker("New WN Message!");
//        notification.setSmallIcon(R.drawable.ic_discuss);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
//                chat, PendingIntent.FLAG_CANCEL_CURRENT);
//        notification.setContentIntent(contentIntent);
//        notification.setAutoCancel(true);
//        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, notification.build());
//    }
}