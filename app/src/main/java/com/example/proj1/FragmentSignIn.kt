@file:Suppress("DEPRECATION")

package com.example.proj1


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proj1.databinding.FragmentSignInBinding
import com.example.proj1.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in.*


class FragmentSignIn : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding : FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val sharedViewModel : SharedViewModel by activityViewModels()

        _binding =  FragmentSignInBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()


        binding.GoToSignUpActivity.isClickable
        binding.GoToSignUpActivity.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentSignUp2)
        }


        binding.signInButton.setOnClickListener {
            val userMail = studentNameSignUp.text.toString()
            val password = binding.studentPassword.text.toString()
            if (userMail.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(userMail, password).addOnCompleteListener {

                    if (it.isSuccessful) {
                        Toast.makeText(context, " success", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_fragmentSignIn_to_permissionFragment)
                    }
                    else if(it.exception.toString() =="com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted."){
                        Toast.makeText(context, "Account not found", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentSignUp2)
                    }
                    else if(it.exception.toString()=="com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."){
                        Toast.makeText(context, "E-mail address badly formatted", Toast.LENGTH_LONG).show()
                    }
                    else if (it.exception.toString() == "com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred."){
                        Toast.makeText(context, "no internet connection", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(context, it.exception.toString(), Toast.LENGTH_LONG).show()
                        Log.d("Firebase" ,it.exception.toString())
                    }

                }

            }
            else{
                sharedViewModel.vibrateDevice(requireContext())
                Toast.makeText(context, "Empty string", Toast.LENGTH_SHORT).show()
            }

        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}