package com.example.proj1.util

object Constants {
    const val Permission_Location_Request_Code = 1
    const val Permission_Background_Location_Request_code = 2

    const val Preference_Name = "geofence_db"
    const val Preference_First_Launch = "FirstLaunch"

    const val NOTIFICATION_CHANNEL_ID = "geofence_transition_id"
    const val NOTIFICATION_CHANNEL_NAME = "geofence_notification"
    const val NOTIFICATION_ID = 3

    const val noInternetConnection : String = "Make sure the device is connected to internet"
    const val Late = "You are late!"
    const val noGPS : String = "Make sure the device GPS is enabled"

    const val invalidEmailException = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."
    const val accountAlreadyExistsException = "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."


}