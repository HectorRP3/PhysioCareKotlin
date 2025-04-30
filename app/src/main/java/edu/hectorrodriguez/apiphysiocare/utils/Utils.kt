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

var pastAppointments = true
var isPhysio = false
var isRecord = false