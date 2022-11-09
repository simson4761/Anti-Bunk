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
import com.example.proj1.dataClass.UserInfo
import com.example.proj1.databinding.FragmentSignUpBinding
import com.example.proj1.util.Constants
import com.example.proj1.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@Suppress("DEPRECATION")
class FragmentSignUp : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var accountStatus : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val sharedViewModel : SharedViewModel by activityViewModels()

        firebaseAuth = FirebaseAuth.getInstance()

        _binding =  FragmentSignUpBinding.inflate(inflater, container, false)

        binding.GoToSignInActivity.isClickable
        binding.GoToSignInActivity.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSignUp2_to_fragmentSignIn)
        }

        binding.signUpButton.setOnClickListener {
            val userName = binding.studentNameSignUp.text
            val registerNumber = binding.studentRegisterNumber.text.toString()
            val eMail = binding.userMail.text.toString()
            val password = binding.password.text.toString()


            val isNotEmpty = eMail.isNotEmpty() && password.isNotEmpty() &&
                             userName.isNotEmpty() && registerNumber.isNotEmpty()

            if (isNotEmpty) {
                    if(registerNumber.toInt() in 39000000..40111999 ){
                        firebaseAuth.createUserWithEmailAndPassword(eMail, password)
                            .addOnCompleteListener {

                            if (it.isSuccessful) {
                                accountStatus = true
                                findNavController().navigate(R.id.action_fragmentSignUp2_to_permissionFragment2)
                            }
                            when(it.exception.toString()){
                                Constants.invalidEmailException ->{
                                    Toast.makeText(context, "Invalid E-mail", Toast.LENGTH_LONG).show()
                                }
                                Constants.accountAlreadyExistsException->{
                                    Toast.makeText(context, "Account already exist", Toast.LENGTH_LONG).show()
                                    findNavController().navigate(R.id.action_fragmentSignUp2_to_fragmentSignIn)
                                }
                                else->{
                                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_LONG).show()
                                    Log.d("Firebase" ,it.exception.toString())
                                }
                            }
                        }
                    }

            }
            else
            {
                sharedViewModel.vibrateDevice(requireContext())
            }

        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if(accountStatus){
            val userName = binding.studentNameSignUp.text
            val registerNumber = binding.studentRegisterNumber.text.toString()
            val databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(" ${firebaseAuth.uid}")
            val userInfo = UserInfo(userName.toString(), registerNumber.toInt(), 100)
            databaseReference.setValue(userInfo)
        }
        _binding = null
    }

}