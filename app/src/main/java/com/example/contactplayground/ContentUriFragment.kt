package com.example.contactplayground

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.contactplayground.databinding.FragmentContentUriBinding

class ContentUriFragment : Fragment() {

    private lateinit var cursorAdapter: SimpleCursorAdapter
    private val loadCallback = MyLoaderCallback()

    private lateinit var binding: FragmentContentUriBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, loadCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentUriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchButton.setOnClickListener { searchOrGetPermission() }
        cursorAdapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            null,
            arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
            intArrayOf(android.R.id.text1),
            0
        )
        binding.searchResults.adapter = cursorAdapter
    }

    private fun searchOnText() {
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, null, loadCallback)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                searchOnText()
            } else {
                Log.e("LDJ", "You need to grant permissions")
            }
        }

    private fun searchOrGetPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED -> {
                searchOnText()
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

    private inner class MyLoaderCallback : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
            /*
             * Makes search string into pattern and
             * stores it in the selection array
             */
            val selectionArgs = arrayOf("%${binding.searchText.text}%")
            // Starts the query

            return activity?.let {
                return CursorLoader(
                    it,
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    null
                )
            } ?: throw IllegalStateException()
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            cursorAdapter.swapCursor(data)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            cursorAdapter.swapCursor(null)
        }

    }

    companion object {
        private const val LOADER_ID = 0

        private val PROJECTION: Array<out String> = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )

        private val SELECTION: String = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    }
}