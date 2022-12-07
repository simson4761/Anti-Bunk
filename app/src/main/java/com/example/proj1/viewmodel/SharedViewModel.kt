package com.example.proj1.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.proj1.DataStoreRepository
import com.example.proj1.broadcastReceiver.GeofenceBroadCastReceiver
import com.example.proj1.util.Constants
import com.example.proj1.util.Permissions
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("DEPRECATION")
@HiltViewModel
class SharedViewModel @Inject constructor(application: Application,
                                          private val dataStoreRepository : DataStoreRepository
    ) : AndroidViewModel(application) {

    private lateinit var builder : AlertDialog.Builder



    val app = application
    val readFirstLaunch = dataStoreRepository.readFirstLaunch.asLiveData()
    private var geofencingClient = LocationServices.getGeofencingClient(app.applicationContext)

    private var geoId: Long = 0L
    private var geoRadius: Float = 500f



    fun saveFirstLaunch(firstLaunch: Boolean) =
        viewModelScope.launch {
            dataStoreRepository.saveFirstLaunch(firstLaunch)
        }

    private fun setPendingIntent(geoId : Int) : PendingIntent{
        val intent = Intent(app , GeofenceBroadCastReceiver :: class.java)
        return PendingIntent.getBroadcast(
            app,
            geoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    @SuppressLint("MissingPermission")
    fun startGeofence(
        latitude: Double,
        longitude: Double,
        context: Context
    ) {

        if (Permissions.hasBackgroundLocationPermissions(app)) {
            val geofence = Geofence.Builder()
                .setRequestId(geoId.toString())
                .setCircularRegion(
                    latitude,
                    longitude,
                    geoRadius
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER
                            or Geofence.GEOFENCE_TRANSITION_EXIT
                            or Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .setLoiteringDelay(5000)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(
                    GeofencingRequest.INITIAL_TRIGGER_ENTER
                            or GeofencingRequest.INITIAL_TRIGGER_EXIT
                            or GeofencingRequest.INITIAL_TRIGGER_DWELL
                )
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build()

            geofencingClient.addGeofences(geofencingRequest, setPendingIntent(geoId.toInt())).run {
                addOnSuccessListener {
                    Log.d("Geofence", "Successfully added.(SharedViewModel)")
                }
                addOnFailureListener {
                    Log.e("Geofence", it.message.toString())
                }
            }


        }
        else
        {
            builder = AlertDialog.Builder(context)
            builder.setTitle("Ooops")
                .setMessage("make sure that location access is set at 'Allow all the Time' ")
                .setCancelable(false)
                .setPositiveButton("grant permission"){ _, _ ->
                    val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(context , intent , null)
                }
                .show()
            Log.d("Geofence", "Permission not granted.")
        }
    }
    fun checkDeviceLocationSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        } else {
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    fun updateAttendance(registerNumber: Int ,
                         imei : String , context: Context) {
        Log.d("Date" , "sharedViewModel Date :${Constants.date}")
        val database = FirebaseDatabase.getInstance().getReference("Attendance").child(Constants.date)
        database.child(imei).setValue(registerNumber)
        Toast.makeText(context , "Attendance updated" , Toast.LENGTH_SHORT).show()

    }

    fun checkDayAndTime(dayOfWeek: String,  time: Int , merit: Int): Boolean {
        return if (dayOfWeek!="Sunday"){
            Log.d("timeCurrent" , "$dayOfWeek $time")
            when(time){
                in 650..2200+merit->{
                    true
                }
                else->{
                    false
                }
            }
        } else{
            false
        }
    }

    fun checkMerit(merit: Int , context : Context) {
        if (merit<=60){
            /*builder.setTitle("Whoa!")
                .setMessage("You are severely lacking merit ")
                .setCancelable(false)
                .setIcon(R.drawable.correct_a_irundhukka)
                .setPositiveButton("okay"){ _, _ ->

                }
                .show()*/
            AlertDialog.Builder(context)
                .setTitle("Whoa!")
                .setMessage("You are severely lacking merit")
                .setCancelable(false)
                .setPositiveButton("okay"){ _,_ ->

                }
                .show()

        }
    }

    fun checkCurrentLocation(currentLatitude: Double,
                             currentLongitude: Double): Boolean {
        return currentLatitude in 12.869667..12.878089 &&
                currentLongitude in 80.215039..80.226464
    }

    fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun vibrateDevice(context: Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val effect: VibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
        Toast.makeText(context, "Empty string", Toast.LENGTH_SHORT).show()
    }

}