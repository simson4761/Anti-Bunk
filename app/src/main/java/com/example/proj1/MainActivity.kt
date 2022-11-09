package com.example.proj1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var geofencingclient : GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {

        geofencingclient = LocationServices.getGeofencingClient(this)
        supportActionBar!!.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}