package learn2crack.gcm;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import learn2crack.activities.WnMessageReceiveActivity;
import learn2crack.chat.R;


public class MSGService extends IntentService {

    SharedPreferences prefs;
    NotificationCompat.Builder notification;
    NotificationManager manager;


    public MSGService() {
        super("MSGService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
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
                    sendNotification(extras.getString("msg_id"),
                                        extras.getString("fromu"),
                                        extras.getString("tab"),
                                        extras.getString("selected_options"),
                                        extras.getString("status"),
                                        extras.getString("type"));
                }
                Log.i("WN", "Received: " + extras.getString("msg_id"));
                Log.i("WN", "MSGService type: " + extras.getString("type"));
            }
        }
        MSGReceiver.completeWakefulIntent(intent);
    }




    private void sendNotification(String msg_id, String from,String tab,String selected_options,
                                  String status , String type) {

        Bundle args = new Bundle();
        args.putString("msg_id", msg_id);
        args.putString("mobno", from);
        args.putString("type", type);
        args.putString("status", "received");
        args.putString("tab", tab);
        args.putString("selected_options", selected_options);
        Intent chat = new Intent(this, WnMessageReceiveActivity.class);
        chat.putExtra("INFO", args);
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("New WN message from " + from);
        //notification.setContentText(msg);
        notification.setTicker("New WN Message!");
        notification.setSmallIcon(R.drawable.ic_done);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                chat, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }


}