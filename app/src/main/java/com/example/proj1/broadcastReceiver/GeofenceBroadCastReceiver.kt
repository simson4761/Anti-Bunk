package com.example.proj1.broadcastReceiver


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.proj1.R
import com.example.proj1.util.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class GeofenceBroadCastReceiver : BroadcastReceiver() {


    private lateinit var firebaseAuth: FirebaseAuth


    private var dayOfWeek : String = ""
    private var date : String = ""
    private var time : Int = 0

    override fun onReceive(context: Context, intent: Intent) {

        val imei = Settings.Secure.getString(context.contentResolver , Settings.Secure.ANDROID_ID)

        //val url = "https://www.timeapi.io/api/Time/current/coordinate?latitude=22.5726&longitude=88.3639"
        //val queue  = Volley.newRequestQueue(context)

        /*val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->
                val jsonArray = JSONObject(response)
                dayOfWeek = jsonArray.getString("dayOfWeek")
                date = jsonArray.getString("date").replace("/","|")
                time = jsonArray.getString("time").replace(":", "").toInt()

            },
            {


                Toast.makeText(context, "error(JSON request)", Toast.LENGTH_SHORT).show()

            }
        )*/



        time = Calendar.HOUR_OF_DAY
        val time2 = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MM|dd|yyyy ")
        date = formatter.format(time2)
        Log.d("dateInstance" , date)




        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()){
                val error = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Toast.makeText(context , error, Toast.LENGTH_LONG).show()
            }

        }
        firebaseAuth = FirebaseAuth.getInstance()

        when(geofencingEvent?.geofenceTransition){

            Geofence.GEOFENCE_TRANSITION_ENTER -> {

                if(checkCollegeTime(dayOfWeek, time)){
                    displayNotification(context , "you have entered" , time)
                    Toast.makeText(context , "You have entered the geofence ", Toast.LENGTH_LONG).show()
                }
                Log.d("Geofence" ,"You have entered the geofence at $time" )

            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {

                if (checkHourOfDay(time)){
                    displayNotification(context , "you have exited" , time)
                    //TODO:alert respective class in-charge
                    meritReduction(firebaseAuth.uid.toString())
                    removeAttendance(date,imei , context)
                    Log.d("BroadCastReceiver Date" , date)

                    Toast.makeText(context , "You have exited the geofence", Toast.LENGTH_LONG).show()
                }
                Log.d("Geofence" ,"You have exited the geofence at $time" )

            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(context , "You are roaming inside geofence", Toast.LENGTH_LONG).show()
                Log.d("Geofence" ,"You are roaming inside geofence at $time" )
                //displayNotification(context , "you are roaming inside")
            }
            else -> {
                Toast.makeText(context , "Beyond comprehension", Toast.LENGTH_LONG).show()
                Log.d("Geofence" ,"else . . . " )
                //displayNotification(context , "something else IDK")
            }
        }


    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun displayNotification(context: Context, geofenceTransition: String , time : Int){
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_directions_walk_24)
            .setContentTitle("Geofence")
            .setContentText("$geofenceTransition at $time")
        notificationManager.notify(Constants.NOTIFICATION_ID, notification.build())
    }

    private fun meritReduction(id: String) {

        val database = FirebaseDatabase.getInstance().getReference("users").child(" $id")
        database.get().addOnSuccessListener {
            if (it.exists()){
                var newMerit = it.child("merit").value.toString().toInt()
                when(newMerit){
                    in 90..100->{
                        newMerit -= 10
                    }
                    in 75..89->{
                        newMerit -= 5
                    }
                    in 51..74->{
                        newMerit = -3
                    }
                    in 20..50->{
                        newMerit -= 1
                    }
                    else->{

                    }
                }

                database.child("merit").setValue(newMerit)
                Log.d("Firebase" , "merit updated $newMerit ")
            }
            else{
                Log.d("Firebase" , "merit not found")
            }
        }


    }

    private fun removeAttendance(
        date: String,
        imei: String,
        context: Context
    ) {
        //TODO : create a room database and store date value from userInterface in that database
        //TODO : So that you never use local time only IST date & time
        Log.d("date" , "broad cast :$date")
        val database = FirebaseDatabase.getInstance().getReference("Attendance").child(" $date")
        database.get().addOnSuccessListener {
            if (it.exists()){
                var attendance = it.child(imei).value.toString()
                attendance = " Bunked :$attendance"

                database.child(imei).setValue(attendance)
                Log.d("Firebase" , "attendance updated $attendance ")
            }
            else{
                Log.d("Firebase" , "attendance not found")
            }
        }
        Toast.makeText(context ,"Attendance revoked" ,Toast.LENGTH_SHORT ).show()
    }

   private fun checkCollegeTime(dayOfWeek : String , time : Int) : Boolean {
        return if (dayOfWeek!="Sunday"){
            Log.d("timeCurrent" , " College Time :$dayOfWeek $time")
            when(time){
                in 900..1100->{
                    true
                }
                in 1215..3000->{
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

   private fun checkHourOfDay(time : Int) : Boolean {
        when(time){
            in 8..15->{
                return true
            }
        }
        return false
   }



}