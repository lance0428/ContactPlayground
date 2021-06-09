package com.example.contactplayground

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import com.example.contactplayground.databinding.FragmentWorkContactsBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WorkContactsFragment : Fragment() {

    //https://developer.android.com/work/contacts
    private lateinit var binding: FragmentWorkContactsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.queryDirectories.setOnClickListener { queryOrGetPermission() }
    }

    private fun queryDirectories() {
        val cursor = requireContext().contentResolver.query(
            ContactsContract.Directory.ENTERPRISE_CONTENT_URI,
            arrayOf(
                ContactsContract.Directory._ID,
                ContactsContract.Directory.PACKAGE_NAME,
                ContactsContract.Directory.DISPLAY_NAME,
                ContactsContract.Directory.ACCOUNT_NAME,
                ContactsContract.Directory.ACCOUNT_TYPE
            ),
            null,
            null,
            null
        )

        // Print the package name of the work profile's local or remote contact directories.
        cursor?.use {
            while (it.moveToNext()) {
                val directoryId = it.getLong(0)
                val packageName = it.getStringOrNull(1)
                val displayName = it.getStringOrNull(2)
                val accountName = it.getStringOrNull(3)
                val accountType = it.getStringOrNull(4)
                Log.i(
                    "LDJ",
                    "Directory id=$directoryId name=$displayName accountName=$accountName accountType=$accountType packageName=$packageName isEnterprise=${
                        ContactsContract.Directory.isEnterpriseDirectoryId(directoryId)
                    } isRemoteDir=${ContactsContract.Directory.isRemoteDirectoryId(directoryId)}"
                )
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                queryDirectories()
            } else {
                Log.e("LDJ", "You need to grant permissions")
            }
        }

    private fun queryOrGetPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                queryDirectories()
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