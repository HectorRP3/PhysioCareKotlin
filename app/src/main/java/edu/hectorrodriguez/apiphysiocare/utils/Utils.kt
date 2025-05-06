package edu.hectorrodriguez.apiphysiocare.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun  checkConnection(context: Context) : Boolean{
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetwork

    if(networkInfo != null){
        val activeNetwork = cm.getNetworkCapabilities(networkInfo)
        if(activeNetwork != null){
            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }

    return false
}

//Para ver si esta en la navegacion de pasado o futuras
var pastAppointments = true
//Para ver si es fisio o paciente
var isPhysio = false
//Para ver si esta en la navegacion de record para que cuando se meta en un record al volver
// para atras no cargue el fragemento de appointments si no el de records
var isRecord = false