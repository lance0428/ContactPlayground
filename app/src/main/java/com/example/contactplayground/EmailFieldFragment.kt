package com.example.contactplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.contactplayground.databinding.FragmentEmailFieldBinding

class EmailFieldFragment : Fragment() {

    private lateinit var binding: FragmentEmailFieldBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailFieldBinding.inflate(inflater, container, false)
        return binding.root
    }
}