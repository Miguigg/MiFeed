package com.tfg.mifeed.controlador.utilidades;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class CheckConexion extends AndroidViewModel {

    public CheckConexion(@NonNull Application application) {
        super(application);
    }

    //para obtener el estado en un momento puntual
    public static Boolean getEstadoActual(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Network network = connectivityManager.getActiveNetwork();
            if ( network == null) return false;
            NetworkCapabilities activeNetwork =
                    connectivityManager.getNetworkCapabilities(network);
            return activeNetwork != null &&
                    (
                            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                    || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                    || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                    || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                    );
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

}
