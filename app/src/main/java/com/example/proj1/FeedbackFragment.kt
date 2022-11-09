package com.example.proj1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proj1.databinding.FragmentFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class FeedbackFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth


    private var _binding : FragmentFeedbackBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance()
        _binding =  FragmentFeedbackBinding.inflate(inflater, container, false)

        val id = firebaseAuth.uid

        val username = FirebaseDatabase.getInstance().getReference("users").child(" $id")







        binding.summitFeedbackButton.setOnClickListener {
            username.get().addOnSuccessListener {

                if(it.exists()){
                    val name: String = it.child("studentName").value.toString()
                    val database = FirebaseDatabase.getInstance().getReference("Feedback").child(name)
                    val feedback = binding.feedbackTextBox.text.toString()
                    database.setValue(feedback)
                    Log.d("Firebase", "Feedback registered " )
                    findNavController().navigate(R.id.action_feedbackFragment_to_userInterface)
                    Toast.makeText(context , "Your feedback has been duly noted :)" , Toast.LENGTH_SHORT).show()
                }

                else {
                    Log.d("Firebase", "account not found : ${firebaseAuth.uid}")
                }

            }.addOnFailureListener {
                Log.d("Firebase", "failed to load account" )
            }
        }
        return binding.root
    }


}