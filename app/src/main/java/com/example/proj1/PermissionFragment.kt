package com.example.proj1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.proj1.databinding.FragmentPermissionBinding
import com.example.proj1.util.ExtensionFunction.observeOnce
import com.example.proj1.util.Permissions
import com.example.proj1.viewmodel.SharedViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class PermissionFragment : Fragment() , EasyPermissions.PermissionCallbacks{

    private var _binding : FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentPermissionBinding.inflate(inflater, container, false)
        if(Permissions.hasLocationPermissions(requireContext()) &&
            Permissions.hasBackgroundLocationPermissions(requireContext())){
            checkFirstLaunch()
        }
        else{
            Permissions.requestLocationPermission(this )
            Permissions.requestBackgroundLocationPermission(this)

        }
        return binding.root
    }

    private fun checkFirstLaunch() {
        sharedViewModel.readFirstLaunch.observeOnce(viewLifecycleOwner) { firstLaunch ->
            if (firstLaunch) {
                findNavController().navigate(R.id.action_permissionFragment_to_aboutFragment)
                sharedViewModel.saveFirstLaunch(false)
            } else {
                findNavController().navigate(R.id.action_permissionFragment_to_userInterface)

            }

        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.permissionPermanentlyDenied(this, perms[0])){
            SettingsDialog.Builder(requireActivity()).build().show()
        }
        else{
            Permissions.requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(requireContext() , "thanks for allowing location permission" , Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_permissionFragment_to_aboutFragment)
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions , grantResults , this)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}