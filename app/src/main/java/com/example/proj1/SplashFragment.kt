package com.example.proj1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proj1.util.ExtensionFunction.observeOnce
import com.example.proj1.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedViewModel : SharedViewModel by activityViewModels()
        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser!=null){
            findNavController().navigate(R.id.action_splashFragment_to_permissionFragment)
        }

        sharedViewModel.readFirstLaunch.observeOnce(viewLifecycleOwner){firstLaunch->
            if(firstLaunch){
                findNavController().navigate(R.id.action_splashFragment_to_fragmentSignIn)
            }
            else{
                findNavController().navigate(R.id.action_splashFragment_to_fragmentSignUp2)
            }
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

}