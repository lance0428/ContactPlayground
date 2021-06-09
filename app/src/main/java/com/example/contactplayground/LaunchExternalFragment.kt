package com.example.contactplayground

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.contactplayground.databinding.FragmentLaunchExternalBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LaunchExternalFragment : Fragment() {

    private lateinit var binding: FragmentLaunchExternalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchExternalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.getContactButton.setOnClickListener { searchOrGetPermission() }
    }

    private fun launchExternal() {
//        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        binding.email.text = ""
        binding.displayName.text = ""
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI)
        startActivityForResult(intent, SELECT_CONTACTS)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_CONTACTS && data?.data != null) {
            Log.e("LDJ", "activity result received Success")
            val contactDataUri = data.data!!

            // below works for ContactsContract.CommonDataKinds.Email.CONTENT_URI
            val projection: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS)
            requireContext().contentResolver.query(contactDataUri, projection, null, null, null)
                .use { cursor ->
                    // If the cursor returned is valid, get the phone number
                    if (cursor?.moveToFirst() == true) {
                        val email =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                        Log.e("LDJ", "   email=$email")
                        binding.email.text = email
                    }
                }
        } else {
            Log.e("LDJ", "activity result received cancel")
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                launchExternal()
            } else {
                Log.e("LDJ", "You need to grant permissions")
            }
        }

    private fun searchOrGetPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                launchExternal()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                Log.e("LDJ", "Show reasoning for permission via custom UI")
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }
    }

    companion object {
        private const val SELECT_CONTACTS = 0

        private val PROJECTION: Array<out String> = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        private val SELECTION: String = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    }
}