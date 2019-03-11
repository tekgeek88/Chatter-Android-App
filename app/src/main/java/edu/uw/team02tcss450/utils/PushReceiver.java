package edu.uw.team02tcss450.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import edu.uw.team02tcss450.HomeActivity;
import edu.uw.team02tcss450.MainActivity;
import edu.uw.team02tcss450.R;
import me.pushy.sdk.Pushy;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class PushReceiver extends BroadcastReceiver {

    public static final String RECEIVED_NEW_MESSAGE = "new message from pushy";

    private static final String CHANNEL_ID = "1";

    @Override
    public void onReceive(Context context, Intent intent) {

        //the following variables are used to store the information sent from Pushy
        //In the WS, you define what gets sent. You can change it there to suit your needs
        //Then here on the Android side, decide what to do with the message you got

        //for the lab, the WS is only sending chat messages so the type will always be msg
        //for your project, the WS need to send different types of push messages.
        //perform so logic/routing based on the "type"
        //feel free to change the key or type of values. You could use numbers like HTTP: 404 etc
        String typeOfMessage = intent.getStringExtra("type");
        Bundle args = new Bundle();
        String sender = "";
        String message = "";
        String chatId = "";

        String fromFirstname = "";
        String fromUsername = "";



        args.putString("type", typeOfMessage);

        if ("msg".equals(typeOfMessage)){
            //The WS sent us the name of the sender
            sender = intent.getStringExtra("sender");
            message = intent.getStringExtra("message");
            chatId = intent.getStringExtra("chat_id");
            args.putString("sender", sender);
            args.putString("message", message);
            args.putString("chat_id", chatId);
            Log.wtf("Pushy Msg", "sender: " + sender + " chatId: " + chatId + " message: " + message);
        } else if ("conn".equals(typeOfMessage)){
            fromFirstname = intent.getStringExtra("fromFirstname");
            fromUsername = intent.getStringExtra("fromUsername");
            message = intent.getStringExtra("message");
            args.putString("fromFirstname", fromFirstname);
            args.putString("fromUsername", fromUsername);
            args.putString("message", message);
            Log.wtf("Pushy conn", "fromFirstname: " + fromFirstname + " fromUsername: " + fromUsername + " message: " + message);
        } else if ("convo".equals(typeOfMessage)){

        }

        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);




        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            //app is in the foreground so send the message to the active Activities
            Log.d("Pushy", "Message received in foreground: ");


//

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("TYPE", typeOfMessage);
            i.putExtra("SENDER", sender);
            i.putExtra("MESSAGE", message);
            i.putExtra("CHAT_ID", chatId);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);

        } else {
            //app is in the background so create and post a notification
            Log.d("Pushy:", "Message received in background: " + typeOfMessage);

            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(intent.getExtras());

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    i, PendingIntent.FLAG_UPDATE_CURRENT);

            //research more on notifications the how to display them
            //https://developer.android.com/guide/topics/ui/notifiers/notifications
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_chat_black_24dp)
                    .setContentTitle("Message from: " + sender)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);

            // Automatically configure a Notification Channel for devices running Android O+
            Pushy.setNotificationChannel(builder, context);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }

    }
}
