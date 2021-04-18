package com.digir.implictintents

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.digir.implictintents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var B: ActivityMainBinding
    val READ_CONTACTS_PERMISSIONS_REQUEST = 1

    companion object {
        val contactList = arrayListOf<String>()
        val contactNumbers = arrayListOf<String>()
        val listID = arrayListOf<String>()

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun getPermission(contentResolver: ContentResolver) {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS
                )
            ) {
            }
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_PERMISSIONS_REQUEST
            )
        } else {
            useCursor(contentResolver)
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val intent = intent
        finish()
        startActivity(intent)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        B = ActivityMainBinding.inflate(layoutInflater)
        setContentView(B.root)

        B.recyclerView.layoutManager = LinearLayoutManager(this)
        B.recyclerView.adapter = MyAdapter()
        val contentResolver = contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getPermission(contentResolver)
        }
        else {
            useCursor(contentResolver)
        }

    }
        fun onClickButtonBrowser(view: View?) {

        val editText = B.editTextBrowse
        val adres = editText.text.toString()
        val uri = Uri.parse("https://www.google.com/search?q=$adres")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
    fun onClickButtonCity(view: View?) {
        val editText = B.editTextCity
        val miasto = editText.text.toString()
        val geoUri = Uri.parse("geo:0,0?q=$miasto")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        startActivity(intent)
    }
}

fun useCursor(contentResolver: ContentResolver) {
    //SELECT * FROM Contacts;
    // ...contestResolver.query return to cursor contact numbers
    val cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        null, null, null, null, null
    )
    if (cursor != null) {
        try {
            cursor.moveToFirst()

            while (!cursor.isAfterLast){
                var id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))  //Id- identyfikator kontaktu
                MainActivity.listID.add(id)
                var name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                MainActivity.contactList.add(name)
                readPhoneNumber(contentResolver, id)
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }
    }
}

fun readPhoneNumber(contentResolver: ContentResolver, id: String) {
    val phoneCursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?", arrayOf(id), null
    )
    if (phoneCursor != null) {
        if(phoneCursor.moveToFirst()) {
            var number = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            MainActivity.contactNumbers.add(number)
        } else
            MainActivity.contactNumbers.add("Brak numeru")
    }
}