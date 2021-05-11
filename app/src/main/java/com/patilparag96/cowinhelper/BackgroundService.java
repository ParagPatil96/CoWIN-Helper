package com.patilparag96.cowinhelper;

import static com.patilparag96.cowinhelper.CowinApi.models.CenterList.*;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.patilparag96.cowinhelper.CowinApi.ApiClient;
import com.patilparag96.cowinhelper.CowinApi.models.CenterList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackgroundService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    NotificationManager notificationManager;
    Notification notification;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            while (true){
                List<Center> availableCenters = findAvailableCenters((SharedData)msg.obj);
                if(!availableCenters.isEmpty()){
                    sendNotification(availableCenters);
                }

                try {
                    Thread.sleep(1000 * 60 * 2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createChannelIfNeeded();

        Toast.makeText(this, "Started Background Task", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.obj = intent.getExtras().getParcelable("data");
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Closed Background Task", Toast.LENGTH_SHORT).show();
    }

    private List<Center> findAvailableCenters(SharedData data){
        CharSequence date  = DateFormat.format("dd-MM-yyyy", new Date().getTime());

        CenterList centerList =  ApiClient.fetchCenters(data.district,date.toString(),data.vaccine);

        List<Center> availableCenters = new ArrayList<>();
        for(Center center: centerList.centers){
            for(Session session: center.sessions){
                if(session.available_capacity>0 && !session.vaccine.equals("COVISHIELD")){
                    availableCenters.add(center);
                }
            }
        }
        return availableCenters;
    }

    private void sendNotification(List<Center> centers){
        Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");

        PendingIntent pendingIntent = PendingIntent.getActivity(BackgroundService.this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(BackgroundService.this);
        builder.setAutoCancel(false);
        builder.setTicker("CoWIN Helper");
        builder.setContentTitle("Open Slot Notification");
        builder.setStyle(new Notification.BigTextStyle().bigText(getNotification(centers)));
        builder.setContentText(getNotification(centers));
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setNumber(centers.size());
        builder.setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId("slot_finder");
        }

        builder.build();

        notification = builder.getNotification();
        notificationManager.notify(1, notification);
    }

    private void createChannelIfNeeded(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "slot_finder";
            CharSequence name = "Slot Finder";
            String Description = "Used for slot notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    private String getNotification(List<Center> centers){
        StringBuilder builder = new StringBuilder("Available Centers: \n");
        for(Center center: centers){
            builder.append(center.getNotification());
        }
        return builder.toString();
    }
}
