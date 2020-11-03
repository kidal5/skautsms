package com.example.skautsmssend

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import android.provider.Telephony
import quanti.com.kotlinlog.Log
import java.util.*


class SMSReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SMSReceiver"
        const val secret = "ahoj"
        val random = Random()
    }


    private fun scheduleJob(context: Context, jobId: Int, telNumber: String, progress: StateOfGame, latencyInSeconds: Long) {
        val extras = PersistableBundle()
        extras.putString(SMSSender.EXTRA_SMS_NUM, telNumber)
        extras.putString(SMSSender.EXTRA_SMS_PROGRESS, progress.name)

        val jobScheduler =
            context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val job = JobInfo.Builder(jobId, ComponentName(context, SMSSender::class.java))
            .setExtras(extras)
            .setMinimumLatency(latencyInSeconds * 1000)
            .build()

        jobScheduler.schedule(job)
        Log.i("Job scheduled $jobId $telNumber $progress $latencyInSeconds")
    }

    private fun getState(context: Context, telNumber: String): StateOfGame {
        val sharedPref = context.getSharedPreferences("global", Context.MODE_PRIVATE)
        if (!sharedPref.contains(telNumber))
            return StateOfGame.NOT_SEEN

        val state = sharedPref.getString(telNumber, "null")!!
        return StateOfGame.valueOf(state)
    }

    private fun isDebug(telNumber: String, text: String): Boolean {
        if (telNumber != "+420602946823") return false
        return StateOfGame.isInside(text)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val message = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0]
        val number = message.originatingAddress ?: "number null"
        val body = message.messageBody ?: "body null"

        if (isDebug(number, body)) return

        Log.i("Received sms from $number with text $body")

        val state = getState(context, number)
        val id = random.nextInt()

        if (state == StateOfGame.REGISTERED || state == StateOfGame.IN_PHASE_THROW || state == StateOfGame.IN_PHASE_BUILD || state == StateOfGame.IN_PHASE_SOMETHING) {
            scheduleJob(context, id, number, StateOfGame.SEND_SMS_IN_GAME, 1)
            return
        }

        if (state == StateOfGame.IN_PHASE_FINISHED) {
            scheduleJob(context, id, number, StateOfGame.SEND_SMS_ALREADY_FINISHED, 1)
            return
        }

        if (state == StateOfGame.NOT_SEEN || state == StateOfGame.WRONG_CODE) {
            //hack for my phone
            if (body != secret) {
                scheduleJob(context, id, number, StateOfGame.WRONG_CODE, 1)
                return
            }
        }

        //secret successful
        scheduleJob(context, id, number, StateOfGame.REGISTERED, 5)
        scheduleJob(context, id + 1, number, StateOfGame.IN_PHASE_THROW, 30)
        scheduleJob(context, id + 2, number, StateOfGame.IN_PHASE_BUILD, 60)
        scheduleJob(context, id + 3, number, StateOfGame.IN_PHASE_SOMETHING, 90)
        scheduleJob(context, id + 4, number, StateOfGame.IN_PHASE_FINISHED, 120)
    }


}