package com.example.skautsmssend

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.base.LoggerBundle
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.bundle.CircleLogBundle


class MainActivity : AppCompatActivity() {

    private val mDataChanged : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            fillTv()
        }
    }

    lateinit var tvKey: TextView
    lateinit var tvValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvKey = findViewById(R.id.tv_key)
        tvValue = findViewById(R.id.tv_value)
        fillTv()

        LocalBroadcastManager.getInstance(this).registerReceiver(mDataChanged, IntentFilter("my_data"))

        Log.initialise(this)
        Log.addLogger(AndroidLogger(LoggerBundle(LogLevel.INFO)))
        Log.addLogger(FileLogger(applicationContext, CircleLogBundle(numOfFiles = 7)))
    }

    override fun onResume() {
        super.onResume()
        fillTv()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataChanged)
    }

    private fun fillTv() {
        var strKey = "";
        var strValue = "";

        val sharedPref = getSharedPreferences("global", Context.MODE_PRIVATE)
        for ((key, value) in sharedPref.all) {
            strKey += "${getContactDisplayNameByNumber(key)}\n"
            strValue += "${value.toString()}\n"
        }

        tvKey.text = strKey
        tvValue.text = strValue
    }

    private fun getContactDisplayNameByNumber(number: String): String {
        val uri: Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var name = number
        val contentResolver = contentResolver
        val contactLookup: Cursor? = contentResolver.query(
            uri, arrayOf(
                BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME
            ), null, null, null
        )
        contactLookup.use {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
            }
        }
        return name
    }

    @SuppressLint("ApplySharedPref")
    fun clearData(view: View?) {
        getSharedPreferences("global", Context.MODE_PRIVATE)
            .edit().clear().commit()

        fillTv()
    }

    fun refreshData(view: View?) {
        fillTv()
    }

    fun sendTrue(view: View?) {
        sendSms(SMSReceiver.secret)
    }

    fun sendFalse(view: View?) {
        sendSms("test spatneho hesla")
    }

    private fun sendSms(smsText: String) {
        val destinationAddress: String = "602946823";

        SmsManager
            .getDefault()
            .sendTextMessage(destinationAddress, null, smsText, null, null)
        Log.i("sms send / $smsText")
    }

}