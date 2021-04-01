package com.example.android.hm_wrk9

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop

import com.example.android.hm_wrk9.databinding.FragmentContactsBinding
import com.google.android.material.textview.MaterialTextView

const val REQUEST_CODE = 42

class ContactsFragment : Fragment() {


    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Доступ к контактам")
                        .setMessage("Объяснение")
                        .setNegativeButton("закрыть") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getContacts() {
        //  Получаем контент реаольвер у контент провайдера
        val contentResolver: ContentResolver = requireContext().contentResolver

        //  Отправляем запрос на получение контактов и получаем ответ в виде КУРСОРА
        val cursorWithContacts: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        if (cursorWithContacts == null) return

        for (i in 0..cursorWithContacts.count) {
            if (cursorWithContacts.moveToPosition(i)) {
                val name = cursorWithContacts.getString(
                    cursorWithContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                )
                val number =
                    cursorWithContacts.getInt(cursorWithContacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toString()
                if (name != null && number != null) {
                    addView(requireContext(), name, number)
                }
            }
        }

        cursorWithContacts?.close()
    }

    private fun addView(context: Context, name: String, number: String) {
        binding.containerForContacts.addView(MaterialTextView(context).apply {
            text = name
            textSize = resources.getDimension(R.dimen.textSize)
        })
        binding.containerForContacts.addView(MaterialTextView(context).apply {
            text = number
            textSize = resources.getDimension(R.dimen.textSize2)
        })
        binding.containerForContacts.addView(View(context).apply {
            background = resources.getDrawable(R.color.black)
//            layoutParams.height = resources.getDimension(R.dimen.viewHeight).toInt()
        })
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) ==
                    PackageManager.PERMISSION_GRANTED -> {
                getContacts()
            }
            // Если нужно пояснение для получения доступа
            else -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Дай доступ к контактам")
                    .setMessage("Так надо")
                    .setPositiveButton("Дать") { _, _ ->
                        requestPermission()
                    }
                    .setNegativeButton("Не давать") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            }
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

    companion object {

        fun newInstance() =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}