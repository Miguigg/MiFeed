package com.tfg.mifeed.controlador.Alarmas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.activities.Activities.BienvenidaActivity;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.FragmentsPodcast.RecordatoriosFragment;
import com.tfg.mifeed.controlador.activities.Activities.Podcast.PodcastMainActivity;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;

import java.security.Provider;
import java.util.List;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i;
        if(FirebaseServices.checkLogin()){
            i = new Intent(context, PodcastMainActivity.class);
            i.putExtra("alarma","redirigeAlarmas");
            context.startActivity(i);
        }else{
            i = new Intent(context, BienvenidaActivity.class);
            context.startActivity(i);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "recordatorioPodcast")
                .setSmallIcon(R.drawable.ic_app_v2)
                .setContentTitle(context.getString(R.string.recordatorioTitulo))
                .setContentText(context.getString(R.string.recordatorioCuerpo))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(1,builder.build());
    }
}
