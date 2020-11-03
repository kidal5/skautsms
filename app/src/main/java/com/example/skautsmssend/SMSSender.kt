package com.example.skautsmssend

import android.R.attr.phoneNumber
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import quanti.com.kotlinlog.Log


class SMSSender : JobService() {

    private fun updatePreferences(context: Context, telNumber: String, progress: StateOfGame) {
        val sharedPref = context.getSharedPreferences("global", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(telNumber, progress.name)
            apply()
            commit()
        }


        val intent = Intent("my_data")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    override fun onStartJob(p0: JobParameters): Boolean {
        val number = p0.extras.getString(EXTRA_SMS_NUM)!!
        val state = StateOfGame.valueOf(p0.extras.getString(EXTRA_SMS_PROGRESS)!!)

        val smsManager: SmsManager = SmsManager.getDefault()

        if (state.text.length <= 159){
            smsManager.sendTextMessage(number, null, state.text, null, null)
        } else {
            val parts = smsManager.divideMessage(state.text)
            smsManager.sendMultipartTextMessage(number, null, parts, null, null)
        }




        Log.i("Send sms to $number with progress ${state.text}")

        if (state != StateOfGame.SEND_SMS_ALREADY_FINISHED && state != StateOfGame.SEND_SMS_IN_GAME)
            updatePreferences(this, number, state)

        jobFinished(p0, false)
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    companion object {
        const val EXTRA_SMS_NUM = "number";
        const val EXTRA_SMS_PROGRESS = "progress"
    }
}