package com.tfg.mifeed.controlador.activities.Activities.Podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tfg.mifeed.R;
import com.tfg.mifeed.controlador.Alarmas.AlarmReceiver;
import com.tfg.mifeed.controlador.firebase.FirebaseServices;
import com.tfg.mifeed.modelo.Episodio;

import java.util.Calendar;

public class CreacionRecordatorioActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static String tituloEpisodio;
    public static String urlAudio;
    public static String urlImagen;
    public static Episodio episodio;

    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;
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
        tituloEpisodio = (String) getIntent().getExtras().get("nombreEpisodio");
        urlAudio = (String) getIntent().getExtras().get("urlAudio");
        urlImagen = (String) getIntent().getExtras().get("urlImagen");
        episodio = new Episodio(tituloEpisodio,urlImagen,urlAudio);
        crearRecordatorio();
    }

    public void crearRecordatorio() {
        Calendar calendar = Calendar.getInstance();
        añoActual = calendar.get(Calendar.YEAR);
        mesActual = calendar.get(Calendar.MONTH);
        diaActual= calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(CreacionRecordatorioActivity.this, CreacionRecordatorioActivity.this, añoActual, mesActual, diaActual);
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreacionRecordatorioActivity.this, CreacionRecordatorioActivity.this, horaActual, minutoActual, DateFormat.is24HourFormat(CreacionRecordatorioActivity.this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
        horaSeleccionada = hora;
        minutoSeleccionado = minuto;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,añoSeleccionado);
        calendar.set(Calendar.MONTH,mesSeleccionado);
        calendar.set(Calendar.DAY_OF_MONTH,diaSeleccionado);
        calendar.set(Calendar.HOUR_OF_DAY,horaSeleccionada);
        calendar.set(Calendar.MINUTE,minutoSeleccionado);
        crearCanal(calendar.getTimeInMillis());
        String input = String.valueOf(añoSeleccionado) + "-" +String.valueOf(mesSeleccionado)+ "-" + String.valueOf(diaSeleccionado) + "T" + String.valueOf(horaSeleccionada) + ":" +String.valueOf(minutoSeleccionado)+":00"+"Z";
        FirebaseServices.setRecordatorio(episodio,input,this.findViewById(android.R.id.content));

        finish();
    }

    public void crearCanal(long timestamp){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String nombreCanal = "notificiacionesMiFeed";
            String descripcion = "Notificaciones para recordar podcast";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("recordatorioPodcast",nombreCanal,importancia);
            channel.setDescription(descripcion);
            NotificationManager notificationManager =getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            FirebaseServices.getNumeroRecordatorio(timestamp,this.findViewById(android.R.id.content));
        }
    }

    public void setAlarma(int id, long timestamp, View v){
        Intent intent = new Intent(v.getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(v.getContext(),id+1 , intent,PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,timestamp,AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(v.getContext(), R.string.recordatorioEstablecido,Toast.LENGTH_LONG).show();
    }
}