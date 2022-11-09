package com.example.proj1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proj1.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {


    private var _binding : FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =  FragmentAboutBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_userInterface)
        }

        return  binding.root

    }



}