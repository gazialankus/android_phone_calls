package com.halilyaman.android_phone_calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log

var isAnswered = false

class PhoneCallHandler : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(AndroidPhoneCallsPlugin.TAG, "onReceive")
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: return
        Log.d(AndroidPhoneCallsPlugin.TAG, "will get caller name")
        var callerName = ""
        var skipDigits = 0;
        while (callerName == "" && skipDigits < 5) {
            callerName = getCallerName(context, phoneNumber, skipDigits) ?: ""
            skipDigits += 1
        }
        Log.d(AndroidPhoneCallsPlugin.TAG, "phoneNumber: $phoneNumber")
        val msg = when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                // Incoming call
                Log.d(AndroidPhoneCallsPlugin.TAG, "Incoming call...")
                hashMapOf("eventType" to "incomingCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                // Call answered
                Log.d(AndroidPhoneCallsPlugin.TAG, "Answered call.")
                isAnswered = true
                hashMapOf("eventType" to "answeredCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                if (isAnswered) {
                    // Call ended
                    Log.d(AndroidPhoneCallsPlugin.TAG, "Ended call.")
                    isAnswered = false
                    hashMapOf("eventType" to "endedCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
                } else {
                    // Call missed or rejected
                    Log.d(AndroidPhoneCallsPlugin.TAG, "Missed/Rejected call.")
                    hashMapOf("eventType" to "missedCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
                }
            }
            else -> hashMapOf("eventType" to "unknown", "phoneNumber" to phoneNumber, "callerName" to callerName)
        }

        val intent = Intent(AndroidPhoneCallsPlugin.LOCAL_INTENT)
        intent.putExtra(AndroidPhoneCallsPlugin.LOCAL_MESSAGE, msg)
        context.sendBroadcast(intent)
    }

    private fun getCallerName(context: Context, phoneNumber: String?, skipDigits: Int): String? {
        Log.d(AndroidPhoneCallsPlugin.TAG, "Caller: will try with $skipDigits ${phoneNumber?.substring(skipDigits)}")
        try {
            var callerName: String? = null
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val selection: String
            val selectionArgs: Array<String>
            if (phoneNumber != null) {
                selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
                selectionArgs = arrayOf(phoneNumber.substring(skipDigits))
            } else {
                selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
                selectionArgs = arrayOf()
            }
            val people = context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null,
            )
            if (people == null) {
                Log.d(AndroidPhoneCallsPlugin.TAG, "Caller: people was null")
            }
            Log.d(AndroidPhoneCallsPlugin.TAG, "Caller: people count: ${people?.count}")
            if (people != null && people.moveToFirst()) {
                val indexName = people.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                if (indexName > -1) {
                    callerName = people.getString(indexName)
                }
                people.close()
            }
            return callerName
        } catch(e: Exception) {
            Log.d(AndroidPhoneCallsPlugin.TAG, "Caller: exception!")
            Log.d(AndroidPhoneCallsPlugin.TAG, e.toString());
            return null
        }
    }
}