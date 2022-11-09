package com.example.proj1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proj1.databinding.FragmentUserInterfaceBinding
import com.example.proj1.util.Constants
import com.example.proj1.viewmodel.SharedViewModel
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vmadalin.easypermissions.EasyPermissions
import org.json.JSONObject
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class UserInterface : Fragment() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var builder : AlertDialog.Builder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding : FragmentUserInterfaceBinding? = null
    private val binding get() = _binding!!
    var currentLatitude : Double = 0.0
    var currentLongitude : Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)






        //

        setLocationListener()

        firebaseAuth = FirebaseAuth.getInstance()
        _binding =  FragmentUserInterfaceBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        builder = AlertDialog.Builder(requireContext())

        val animationTopToBottom = AnimationUtils.loadAnimation(context , R.anim.toptobottom)
        val animationLeftToRight = AnimationUtils.loadAnimation(context , R.anim.left_to_right)

        binding.hola.startAnimation(animationTopToBottom)
        binding.button.startAnimation(animationLeftToRight)



        val url = "https://www.timeapi.io/api/Time/current/coordinate?latitude=22.5726&longitude=88.3639"
        val queue  = Volley.newRequestQueue(context)

        var dayOfWeek = "Sunday"
        var date = "123"
        var time = 1000

        val imei = Settings.Secure.getString(context!!.contentResolver , Settings.Secure.ANDROID_ID)
        Log.d("IMEI", imei)

        val id = firebaseAuth.uid
        val database = FirebaseDatabase.getInstance().getReference("users").child(" $id")

        val sharedViewModel : SharedViewModel by activityViewModels()

        var name : String
        var merit  = 0
        var registerNumber by Delegates.notNull<Int>()


        val adf  = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager?


        Log.d("GPS" , adf!!.isLocationEnabled.toString())

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null){
                currentLatitude = it.latitude
                currentLongitude = it.longitude
                Log.d("Geofence" , " fused location : current latitude : $currentLatitude" +
                        "current longitude : $currentLongitude")
            }
            if(it==null){
                setLocationListener()
                Log.d("Geofence" , "couldn't load current location ")
            }
        }.addOnFailureListener {
            Log.d("Geofence" , "couldn't load current location ")
        }

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val jsonArray = JSONObject(response)
            dayOfWeek = jsonArray.getString("dayOfWeek")
            date = jsonArray.getString("date").replace("/","|")
            time = jsonArray.getString("time").replace(":", "").toInt()
            Log.d("dateInstance" , "$date $time")

            },
            {
                Toast.makeText(context, "error(JSON request)", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(context).setMessage(Constants.noInternetConnection)
                    .setCancelable(false)
                    .setPositiveButton("okay"){_,_ ->
                    startActivityForResult( Intent(Settings.ACTION_WIFI_SETTINGS), 0)
                }.show()
                Log.d("JSONTime" , it.toString())
            }
        )
        queue.add(stringRequest)

        database.get().addOnSuccessListener {
            if(it.exists()){
                name  = it.child("studentName").value.toString()
                merit = it.child("merit").value.toString().toInt()
                registerNumber = it.child("registerNo").value.toString().toInt()
                sharedViewModel.checkMerit(merit, requireContext())
                binding.hola.text = "hello $name"
            }
            else
            {
                Log.d("Firebase", "account not found : ${firebaseAuth.uid}")
                Toast.makeText(context, "account not found", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Log.d("Firebase", "failed to load account")
            Toast.makeText(context , it.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.button.setOnClickListener {

            setLocationListener()
            if (!adf.isLocationEnabled){
                AlertDialog.Builder(context).setMessage(Constants.noGPS)
                    .setCancelable(false)
                    .setPositiveButton("okay"){_,_ ->
                        startActivityForResult( Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
                    }.show()
            }
            Log.d("Geofence" , "current latitude : $currentLatitude" +
                    "current longitude : $currentLongitude")

            val isHePunctual : Boolean = sharedViewModel.checkDayAndTime(dayOfWeek , time, merit) &&
                    sharedViewModel.checkCurrentLocation(currentLatitude , currentLongitude)
            if(!sharedViewModel.checkForInternet(requireContext())){
                Toast.makeText(context , Constants.noInternetConnection , Toast.LENGTH_LONG).show()
            }
            if(!EasyPermissions.hasPermissions(context, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(context , " please give permission" , Toast.LENGTH_LONG).show()
            }
            if (sharedViewModel.checkDeviceLocationSettings(requireContext())){
                if(isHePunctual){
                    sharedViewModel.startGeofence(
                        12.8737445 ,
                        80.2213032 ,
                         requireContext())
                    Log.d("Geofence", "method added in aaa_frag. ")
                    sharedViewModel.updateAttendance(date ,registerNumber, imei , requireContext())
                }
                else if(!sharedViewModel.checkCurrentLocation(currentLatitude , currentLongitude)){
                    AlertDialog.Builder(context).setMessage("you are not inside college premise").show()
                }
                else if(!sharedViewModel.checkDayAndTime(dayOfWeek , time , merit)){
                    Toast.makeText(context, Constants.Late, Toast.LENGTH_SHORT).show()
                    AlertDialog.Builder(context).setMessage(Constants.Late).show()
                }
            }

        }

        binding.menu.setOnClickListener{
            binding.menu.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_arrow_back_24))
            val popupMenu = PopupMenu(context!!,binding.menu)
            popupMenu.menuInflater.inflate(R.menu.menu_user_interface , popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.log_out ->
                        signOut()
                    R.id.feedback ->
                        feedback()
                    R.id.about ->
                        about()
                    R.id.support ->
                        support()
                }
                true
            }
            popupMenu.show()
        }

        return binding.root
        }

    private fun support() {
        //TODO("Not yet implemented")
        binding.menu.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_menu_24))
    }

    private fun about() {
        binding.menu.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_menu_24))
        findNavController().navigate(R.id.action_userInterface_to_aboutFragment)
    }

    private fun feedback() {
        binding.menu.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_menu_24))
        findNavController().navigate(R.id.action_userInterface_to_feedbackFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_userInterface_to_splashFragment)
        binding.menu.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_menu_24))
    }

    private fun setLocationListener()  {

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(5000).setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude

                    }
                    // Things don't end here
                    Log.d("Geofence" , "current latitude fn : $currentLatitude" +
                            "current longitude  fn : $currentLongitude")
                }
            },
            Looper.getMainLooper()
        )

    }


}