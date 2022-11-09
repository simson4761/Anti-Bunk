package com.example.proj1.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.example.proj1.util.Constants.Permission_Background_Location_Request_code
import com.example.proj1.util.Constants.Permission_Location_Request_Code
import com.vmadalin.easypermissions.EasyPermissions


object Permissions {
    fun hasLocationPermissions(context : Context) =
            EasyPermissions.hasPermissions(context ,
              Manifest.permission.ACCESS_FINE_LOCATION)

    fun requestLocationPermission(fragment : Fragment){
        EasyPermissions.requestPermissions(fragment,
            "this permission is mandatory",
            Permission_Location_Request_Code,
            Manifest.permission.ACCESS_FINE_LOCATION)

    }

    fun hasBackgroundLocationPermissions(context : Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else{
            true
        }


    }
    fun requestBackgroundLocationPermission(fragment : Fragment){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(fragment,
                "this permission is mandatory",
                Permission_Background_Location_Request_code,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }


    }

    /*fun hasReadPhonePermission(context : Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.READ_PHONE_STATE,
        )

    fun requestReadPhonePermission(fragment : Fragment){
        EasyPermissions.requestPermissions(fragment,
            "this permission is mandatory",
            Permission_Location_Request_Code,
            Manifest.permission.READ_PHONE_STATE)

    }*/

}