package com.tfg.mifeed.controlador.activities.Activities.Podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Alarmas.AlarmReceiver;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.LoginActivity;
import com.tfg.mifeed.controlador.activities.Activities.GestionCuenta.RegistroActivity;
import com.tfg.mifeed.controlador.firebase.FirebasePodcast;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.controlador.utilidades.CheckConexion;import com.tfg.mifeed.modelo.Episodio;

import org.w3c.dom.Text;

import java.util.Calendar;

public class CreacionRecordatorioActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static String tituloEpisodio;
    public static String urlAudio;
    public static String urlImagen;
    public static Episodio episodio;
    public static String idPodcast;
    public static TextView errPermisos;
    public static AlarmManager alarmManager;
    public static NotificationManager notificationManager;
    public static View v;
    public static PendingIntent pendingIntent;
    public FirebasePodcast firebasePodcast;
    public static int añoSeleccionado,
            mesSeleccionado,
            diaSeleccionado,
            horaSeleccionada,
            minutoSeleccionado,
            añoActual,
            mesActual,
            diaActual,
            horaActual,
            minutoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion_recordatorio);
        firebasePodcast = new FirebasePodcast();

        tituloEpisodio = (String) getIntent().getExtras().get("nombreEpisodio");
        urlAudio = (String) getIntent().getExtras().get("urlAudio");
        urlImagen = (String) getIntent().getExtras().get("urlImagen");
        idPodcast = (String) getIntent().getExtras().get("idPodcast");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationManager = getSystemService(NotificationManager.class);
        v = this.findViewById(android.R.id.content);
        errPermisos = findViewById(R.id.errPermisos);
        episodio = new Episodio(tituloEpisodio,urlImagen,urlAudio);
        checkRecordatoriosActivos();
    }

    private void checkRecordatoriosActivos() {
        FirebasePodcast.checkRecordatorios();
    }

    public void respuestaRecordatorios(String res){
        switch (res){
            case "true":
                errPermisos.setVisibility(View.GONE);
                crearRecordatorio();
                break;
            case "false":
                errPermisos.setVisibility(View.VISIBLE);
        }
    }

    public void crearRecordatorio() {
        Calendar calendar = Calendar.getInstance();
        añoActual = calendar.get(Calendar.YEAR);
        mesActual = calendar.get(Calendar.MONTH);
        diaActual= calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(v.getContext(), CreacionRecordatorioActivity.this, añoActual, mesActual, diaActual);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
        añoSeleccionado = año;
        mesSeleccionado = mes;
        diaSeleccionado = dia;
        Calendar c = Calendar.getInstance();
        horaActual = c.get(Calendar.HOUR);
        minutoActual = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), CreacionRecordatorioActivity.this, horaActual, minutoActual, DateFormat.is24HourFormat(v.getContext()));
        timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                v.getContext().startActivity(new Intent(v.getContext(), PodcastMainActivity.class));
                finish();
            }
        });
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
        horaSeleccionada = hora;
        minutoSeleccionado = minuto;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,añoSeleccionado);
        calendar.set(Calendar.MONTH,mesSeleccionado);
        calendar.set(Calendar.DAY_OF_MONTH,diaSeleccionado);
        calendar.set(Calendar.HOUR_OF_DAY,horaSeleccionada);
        calendar.set(Calendar.MINUTE,minutoSeleccionado);
        String input = String.valueOf(añoSeleccionado) + "-" +String.valueOf(mesSeleccionado)+ "-" + String.valueOf(diaSeleccionado) + "T" + String.valueOf(horaSeleccionada) + ":" +String.valueOf(minutoSeleccionado)+":00"+"Z";
        crearCanal(calendar.getTimeInMillis());
        FirebasePodcast.setRecordatorio(episodio,input,v, idPodcast);
        if(!CheckConexion.getEstadoActual(CreacionRecordatorioActivity.this)){
            Toast.makeText(CreacionRecordatorioActivity.this,R.string.txtRecordatorioSinConexion,Toast.LENGTH_LONG).show();
        }
        ((Activity) v.getContext()).finish();
    }

    public void crearCanal(long timestamp){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String nombreCanal = "notificiacionesMiFeed";
            String descripcion = "Notificaciones para recordar podcast";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("recordatorioPodcast",nombreCanal,importancia);
            channel.setDescription(descripcion);
            notificationManager.createNotificationChannel(channel);
            FirebasePodcast.getNumeroRecordatorio(timestamp,v);
        }
    }

    public void setAlarma(int id, long timestamp, View v){
        Intent intent = new Intent(v.getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(v.getContext(),id+1 , intent,PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,timestamp,AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}