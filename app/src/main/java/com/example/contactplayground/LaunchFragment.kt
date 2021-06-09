package com.example.contactplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.contactplayground.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    private lateinit var binding: FragmentLaunchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.contentUriButton.setOnClickListener {
            findNavController().navigate(R.id.action_LaunchFragment_to_FirstFragment)
        }
        binding.contentFilterUriButton.setOnClickListener {
            findNavController().navigate(R.id.action_LaunchFragment_to_contentFilterUriFragment)
        }
        binding.launchExternalButton.setOnClickListener {
            findNavController().navigate(R.id.action_LaunchFragment_to_launchExternalFragment)
        }
        binding.workContactsButtons.setOnClickListener {
            findNavController().navigate(R.id.action_LaunchFragment_to_workContactsFragment)
        }
    }
}