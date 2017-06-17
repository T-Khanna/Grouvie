package jett_apps.grouvie.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jett_apps.grouvie.HelperObjects.Friend;
import jett_apps.grouvie.HelperObjects.PropagationObject;
import jett_apps.grouvie.R;
import jett_apps.grouvie.Views.LandingPage;

import static jett_apps.grouvie.Views.LandingPage.SENT_PLAN;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "GrouvieMsgService";
    private PropagationObject plan = new PropagationObject();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        JSONObject planInJSON = new JSONObject(remoteMessage.getData());
        try {
            String film = planInJSON.getString("chosenFilm");
            String cinema = planInJSON.getString("chosenCinema");
            String showtime = planInJSON.getString("showtime");
            String date = planInJSON.getString("date");
            String[] friendNames = planInJSON.getString("friend_list").split(",");
            String[] friendNumbers = planInJSON.getString("friends").split(",");
            ArrayList<Friend> members = new ArrayList<>();
            int numOfContacts = friendNames.length;
            for (int i = 0; i < numOfContacts; i++) {
                String name = friendNames[i];
                String number = friendNumbers[i];
                if (i == 0) {
                    name = name.substring(1);
                    number = number.substring(1);
                } else if (i == numOfContacts - 1) {
                    name = name.substring(0, name.length() - 1);
                    number = number.substring(0, number.length() - 1);
                }
                members.add(new Friend(name, number));
            }
            String leaderPhoneNum = planInJSON.getString("leader");
            plan.setFilmTitle(film);
            plan.setCinemaData(cinema);
            plan.setChosenTime(showtime);
            plan.setDate(date);
            plan.setSelectedFriends(members);
            plan.setLeaderPhoneNumber(leaderPhoneNum);
        } catch (JSONException e) {
            Log.e("BAD JSON SENT", "JSON sent to user was not an event plan");
            e.printStackTrace();
        }
        //Calling method to generate notification
        String messageBody = "Your friend has created a new plan!";
        sendNotification(messageBody);
    }

    //This method to generate push notification
    private void sendNotification(String messageBody) {
        //MainActivity Intent Registration
        Intent intent = new Intent(this, LandingPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(SENT_PLAN, plan);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        //Take Notification Sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Generate the Notification
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Grouvie Time")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
        //Create Push Notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}